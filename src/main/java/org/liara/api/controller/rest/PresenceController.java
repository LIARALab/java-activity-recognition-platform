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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.CursorBasedIterator;
import org.liara.api.collection.EntityCollectionQuery;
import org.liara.api.collection.EntityCollections;
import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.data.entity.BooleanState;
import org.liara.api.data.entity.Presence;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIRequestFreeCursorParser;
import org.liara.api.request.validator.APIRequestFreeCursorValidator;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.liara.recognition.presence.Tick;
import org.liara.recognition.presence.TickEventStream;
import org.liara.recognition.presence.LiaraPresenceStream;
import org.liara.recognition.presence.StateTickStream;
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
  private EntityManager _entityManager;
  
  @Autowired
  private EntityCollections _collections;
  
  @Autowired
  private NodeRepository _nodes;
  
  @Autowired
  private ApplicationContext _context;
  
  private Iterator<Presence> presences (final long identifier) throws EntityNotFoundException {
    final Sensor sensor = _collections.createCollection(Sensor.class).findByIdOrFail(identifier);
    final List<Presence> result = new ArrayList<>();
    final List<Sensor> sensors = _nodes.getAllSensors(sensor.getNodes(), "common/native/motion");
    
    final EntityCollectionQuery<BooleanState, BooleanState> stateQuery = _collections.createQuery(BooleanState.class);
    stateQuery.where(stateQuery.join("sensor").in(sensors));
    stateQuery.orderBy(_entityManager.getCriteriaBuilder().asc(stateQuery.field("date")));

    return new LiaraPresenceStream(new TickEventStream(new StateTickStream(_collections.fetch(stateQuery))));
  }
  
  @GetMapping("/sensors/{identifier}/presences")
  public List<Presence> index (
    @NonNull final HttpServletRequest request, 
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    
    assertIsValidRequest(apiRequest, new APIRequestFreeCursorValidator());
    
    final List<Presence> result = new ArrayList<>();
    
    final Iterator<Presence> presences = CursorBasedIterator.apply(
      (new APIRequestFreeCursorParser()).parse(apiRequest), 
      this.presences(identifier)
    );

    Iterators.addAll(result, presences);
    
    return result;
  }
  
  @GetMapping("/sensors/{identifier}/presences/sum")
  public Map<String, Duration> sum (
    @NonNull final HttpServletRequest request, 
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    this.assertIsValidRequest(apiRequest, new APIRequestFreeCursorValidator());
    
    final Iterator<Presence> presences = CursorBasedIterator.apply(
      (new APIRequestFreeCursorParser()).parse(apiRequest), 
      this.presences(identifier)
    );
    
    final Map<String, Duration> result = new HashMap<>();
    
    while (presences.hasNext()) {
      final Presence next = presences.next();
      if (result.containsKey(next.getRoomName())) {
        result.put(next.getRoomName(), result.get(next.getRoomName()).plus(next.getDuration()));
      } else {
        result.put(next.getRoomName(), next.getDuration());
      }
    }
    
    return result;
  }
  
  @GetMapping("/sensors/{identifier}/presences/avg")
  public Map<String, Duration> avg (
    @NonNull final HttpServletRequest request, 
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    this.assertIsValidRequest(apiRequest, new APIRequestFreeCursorValidator());
    
    final Iterator<Presence> presences = CursorBasedIterator.apply(
      (new APIRequestFreeCursorParser()).parse(apiRequest), 
      this.presences(identifier)
    );
    
    final Map<String, Duration> sums = new HashMap<>();
    final Map<String, Long> counts = new HashMap<>();
    
    while (presences.hasNext()) {
      final Presence next = presences.next();
      if (sums.containsKey(next.getRoomName())) {
        sums.put(next.getRoomName(), sums.get(next.getRoomName()).plus(next.getDuration()));
        counts.put(next.getRoomName(), counts.get(next.getRoomName()) + 1);
      } else {
        sums.put(next.getRoomName(), next.getDuration());
        counts.put(next.getRoomName(), 1L);
      }
    }
    
    final Map<String, Duration> result = new HashMap<>();
    
    for (final String key : sums.keySet()) {
      result.put(key, sums.get(key).dividedBy(counts.get(key)));
    }
    
    return result;
  }
  
  /*
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

    final StateTickStream ticks = new StateTickStream(_collections.fetch(stateQuery).iterator());
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
  */
}
