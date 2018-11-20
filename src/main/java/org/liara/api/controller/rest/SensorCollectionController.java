/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
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

import io.swagger.annotations.Api;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionFactory;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(
    tags = {
      "sensor"
    },
    description = "",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class SensorCollectionController
  extends BaseRestController
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final NodeRepository _nodeRepository;

  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @Autowired
  public SensorCollectionController (
    @NonNull final EntityManager entityManager,
    @NonNull final NodeRepository nodeRepository,
    @NonNull final ApplicationEventPublisher applicationEventPublisher,
    @NonNull final CollectionFactory collections
  )
  {
    super(collections);
    _applicationEventPublisher = applicationEventPublisher;
    _nodeRepository = nodeRepository;
    _entityManager = entityManager;
  }

  @GetMapping("/sensors/count")
  public @NonNull Long count (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return count(Sensor.class, request);
  }

  @GetMapping("/sensors")
  public @NonNull ResponseEntity<@NonNull List<@NonNull Sensor>> index (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return index(Sensor.class, request);
  }

  @GetMapping("/sensors/{identifier}")
  public @NonNull Sensor get (
    @PathVariable final Long identifier
  )
  {
    return get(Sensor.class, identifier);
  }

  @PostMapping("/sensors")
  @Transactional
  public ResponseEntity<Void> create (
    @NonNull final HttpServletRequest request, @NonNull @Valid @RequestBody final Sensor sensor
  )
  {
    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, sensor));

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Location", request.getRequestURI() + "/" + sensor.getIdentifier());
    
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping("/sensors/{identifier}/node")
  public @NonNull Node getNode (
    @NonNull final HttpServletRequest request, @PathVariable @NonNull final Long identifier
  )
  {
    return _nodeRepository.find(_entityManager.find(Sensor.class, identifier).getNodeIdentifier()).get();
  }

  /*
  @GetMapping("/sensors/{identifier}/states")
  public @NonNull ResponseEntity<@NonNull Set<@NonNull State>> getStates (
    @NonNull final HttpServletRequest request,
    @PathVariable @NonNull final Long identifier
  ) {
    return toResponse(apply(
      new JPAEntityCollection<>(_entityManager, State.class),
      _configuration,
      request
    ));
  }
  
  @GetMapping("/sensors/{identifier}/states/count")
  public ResponseEntity<Object> countStates (
    @NonNull final HttpServletRequest request,
    @PathVariable @NonNull final Long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {    
    return aggregate(
      _states.of(_sensors.findByIdentifierOrFail(sensorIdentifier)), 
      request,
      EntityCountAggregationTransformation.instantiate()
    );
  }
  
  @GetMapping("/sensors/{sensorIdentifier}/states/{stateIdentifier}")
  public State getState (
    @NonNull final HttpServletRequest request,
    @PathVariable final long sensorIdentifier,
    @PathVariable final long stateIdentifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {    
    return _states.of(
      _sensors.findByIdentifierOrFail(sensorIdentifier)
    ).findByIdentifierOrFail(stateIdentifier);
  }
  */
}
