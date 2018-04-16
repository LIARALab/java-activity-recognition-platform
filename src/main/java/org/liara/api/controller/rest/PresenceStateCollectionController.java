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
package org.liara.api.controller.rest;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.liara.api.collection.EntityCollections;
import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.PresenceStateCollection;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.PresenceState;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.liara.recognition.presence.TickEventStream;
import org.liara.recognition.presence.LiaraPresenceStream;
import org.liara.recognition.presence.StateTickStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;

@RestController
@Api(
    tags = {
      "states<presence>"
    },
    description = "",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class PresenceStateCollectionController extends BaseRestController
{
  @Autowired
  private EntityManager _entityManager;
  
  @Autowired
  private EntityCollections _collections;
  
  @Autowired
  private NodeCollection _nodes;
  
  @Autowired
  private PresenceStateCollection _collection;
  
  private Iterator<PresenceState> presences (@NonNull final Sensor sensor) throws EntityNotFoundException {
    final List<Sensor> sensors = _nodes.getAllSensors(sensor.getNodes(), "common/native/motion");
    
    final EntityCollectionQuery<BooleanState, BooleanState> stateQuery = _collections.createQuery(BooleanState.class);
    stateQuery.where(stateQuery.joinCollection("_sensor").getEntity().in(sensors));
    stateQuery.orderBy(_entityManager.getCriteriaBuilder().asc(stateQuery.getEntity().get("_emittionDate")));

    return new LiaraPresenceStream(new TickEventStream(new StateTickStream(_collections.fetch(stateQuery))));
  }
  
  @GetMapping("/sensors/{identifier}/refresh")
  @Transactional(rollbackOn = {Error.class, Exception.class})
  public void refresh (
    @NonNull final HttpServletRequest request, 
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException
  {
    final Sensor sensor = _collections.createCollection(Sensor.class).findByIdOrFail(identifier);
    
    if (!sensor.getType().equals("common/virtual/presence")) {
      throw new Error("Not a presence sensor.");
    }
    
    final Iterator<PresenceState> presences = this.presences(sensor);
    
    while (presences.hasNext()) {
      final PresenceState presence = presences.next();
      presence.setSensor(sensor);

      _entityManager.persist(presence);
    }
  }
  
  @GetMapping("/states<presence>")
  public ResponseEntity<List<PresenceState>> index (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return indexCollection(_collection, request);
  }
  
  @GetMapping("/states<presence>/{identifier}")
  public PresenceState index (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException
  {
    return _collection.findByIdOrFail(identifier);
  }
  

  @GetMapping("/states<presence>/count")
  public ResponseEntity<Object> count (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(_collection, request, this::count);
  }
  
  @GetMapping("/states<presence>/sum")
  public ResponseEntity<Object> sum (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(_collection, request, this::sum, x -> Duration.ofMillis(x));
  }
  
  @GetMapping("/states<presence>/avg")
  public ResponseEntity<Object> avg (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(_collection, request, this::avg, x -> Duration.ofMillis(x.longValue()));
  }
  
  private Expression<Long> sum (
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Path<?> root
  ) {
    return builder.sumAsLong(root.get("_milliseconds"));
  }
  
  private Expression<Double> avg (
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Path<?> root
  ) {
    return builder.avg(root.get("_milliseconds"));
  }
}
