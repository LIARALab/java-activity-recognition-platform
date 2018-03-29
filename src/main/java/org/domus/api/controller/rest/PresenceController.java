/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.domus.api.controller.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;

import org.domus.api.collection.EntityCollectionQuery;
import org.domus.api.collection.EntityCollections;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.data.entity.BooleanState;
import org.domus.api.data.entity.Node;
import org.domus.api.data.entity.Presence;
import org.domus.api.data.entity.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(
    tags = {
      "presence"
    },
    description = "",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class PresenceController
{
  @Autowired
  private EntityCollections _collections;
  
  @GetMapping("/presences")
  public List<Presence> index (@NonNull final HttpServletRequest request) throws EntityNotFoundException
  {
    final List<Presence> presences = new ArrayList<>();
    final CriteriaBuilder criteriaBuilder = _collections.getEntityManager().getCriteriaBuilder();
    
    final Node appartement = _collections.createCollection(Node.class).findByIdOrFail(7);
    final EntityCollectionQuery<Node, Node> roomsQuery = _collections.createQuery(Node.class);
    roomsQuery.where(criteriaBuilder.gt(roomsQuery.field("start").as(Integer.class), appartement.getStart()));
    roomsQuery.where(criteriaBuilder.lt(roomsQuery.field("end").as(Integer.class), appartement.getEnd()));
    
    final List<Node> rooms = _collections.fetch(roomsQuery);
    
    final EntityCollectionQuery<Sensor, Sensor> sensorQuery = _collections.createQuery(Sensor.class);
    sensorQuery.where(sensorQuery.join("nodes").in(rooms));
    
    final List<Sensor> sensors = _collections.fetch(sensorQuery);
    final EntityCollectionQuery<BooleanState, BooleanState> stateQuery = _collections.createQuery(BooleanState.class);
    stateQuery.where(stateQuery.join("sensor").in(sensors));
    stateQuery.orderBy(criteriaBuilder.asc(stateQuery.field("date")));
    
    final Iterator<BooleanState> states = _collections.fetch(stateQuery).iterator();
    
    Presence lastPresence = null;
    BooleanState last = null;
    
    while (states.hasNext()) {
      final BooleanState next = states.next();
      if (last == null || last.getValue() == false) {
        if (next.getValue() == true) {
          last = next;
        }
      } else if (last != null && last.getValue() == true) {
        if (next.getValue() == false || !next.getSensor().equals(last.getSensor())) {
          final Presence presence = new Presence(last.getDate(), next.getDate(), last.getSensor().getNodes().get(0));
          
          if (lastPresence == null) {
            lastPresence = presence;
          } else if (lastPresence.getRoom().equals(presence.getRoom())) {
            lastPresence = lastPresence.merge(presence);
          } else {
            presences.add(lastPresence);
            lastPresence = presence;
          }
          last = next;
        }
      }
    }
    
    if (lastPresence != null) {
      presences.add(lastPresence);
    }
    
    return presences;
  }
}
