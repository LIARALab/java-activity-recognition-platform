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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.domus.api.collection.CursorBasedIterator;
import org.domus.api.collection.EntityCollectionQuery;
import org.domus.api.collection.EntityCollections;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.data.entity.BooleanState;
import org.domus.api.data.entity.Presence;
import org.domus.api.data.entity.Sensor;
import org.domus.api.data.repository.NodeRepository;
import org.domus.api.request.APIRequest;
import org.domus.api.request.parser.FreeCursorParser;
import org.domus.api.request.validator.FreeCursorValidator;
import org.domus.api.request.validator.error.InvalidAPIRequestException;
import org.domus.recognition.PresenceRecognition;
import org.domus.recognition.Tick;
import org.domus.recognition.Ticks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Iterators;

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
public class PresenceController extends BaseRestController
{
  @Autowired
  private EntityCollections _collections;
  
  @Autowired
  private NodeRepository _nodes;
  
  @Autowired
  private ApplicationContext _context;
  
  @GetMapping("/sensors/{identifier}/presences")
  public List<Presence> index (
    @NonNull final HttpServletRequest request, 
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException
  {
    final Sensor sensor = _collections.createCollection(Sensor.class).findByIdOrFail(identifier);
    final List<Presence> result = new ArrayList<>();
    final APIRequest apiRequest = APIRequest.from(request);
    
    this.assertIsValidRequest(apiRequest, new FreeCursorValidator());
    
    final Iterator<Presence> presences = CursorBasedIterator.apply(
      (new FreeCursorParser()).parse(apiRequest), 
      new PresenceRecognition(_context, sensor)
    );
    
    Iterators.addAll(result, presences);
    
    return result;
  }
  

  @GetMapping("/sensors/{identifier}/q1")
  public Map<String, Duration> indexQ1 (
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException
  {
    final Sensor sensor = _collections.createCollection(Sensor.class).findByIdOrFail(identifier);
    final List<Sensor> sensors = _nodes.getAllSensors(sensor.getNodes(), "common/native/motion");

    final EntityCollectionQuery<BooleanState, BooleanState> stateQuery = _collections.createQuery(BooleanState.class);
    stateQuery.where(stateQuery.join("sensor").in(sensors));
    stateQuery.orderBy(_collections.getEntityManager().getCriteriaBuilder().asc(stateQuery.field("date")));

    final Ticks ticks = new Ticks(_collections.fetch(stateQuery).iterator());
    final List<Duration> durations = new ArrayList<>();
    final Map<Sensor, Tick> lastTicks = new HashMap<>();
    
    while (ticks.hasNext()) {
      final Tick tick = ticks.next();
      final Tick previous = (lastTicks.containsKey(tick.getSensor())) ? lastTicks.get(tick.getSensor()) : null;
      
      if (previous != null && tick.getDate().compareTo(previous.getDate()) < 0) {
        throw new Error("Invalid tick order.");
      }
      
      if (tick.isUp()) {
        if (previous == null || previous.isDown()) {
          lastTicks.put(tick.getSensor(), tick);
        } else if (previous != null && previous.isUp()) {
          throw new Error("Invalid tick");
        }
      } else {
        if (previous == null || previous.isDown()) {
          throw new Error("Invalid tick");
        }
        
        final Duration duration = Duration.between(previous.getDate(), tick.getDate());
        durations.add(duration);
        lastTicks.put(tick.getSensor(), tick);
      }
    }
    
    Collections.sort(durations);
    
    final Map<String, Duration> qts = new HashMap<>();
    final int q = durations.size() / 4;
    
    qts.put("q1", durations.get(0));
    qts.put("q2", durations.get(q));
    qts.put("q3", durations.get(q * 2));
    qts.put("q4", durations.get(q * 3));
    qts.put("q5", durations.get(durations.size() - 1));
    
    return qts;
  }
}
