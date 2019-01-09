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
package org.liara.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.collection.CollectionController;
import org.liara.api.metamodel.collection.PostableCollectionController;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.validation.InvalidModelException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@CollectionController.Name("sensors")
public class SensorCollectionController
  extends ApplicationEntityCollectionController<Sensor>
  implements PostableCollectionController<Sensor>
{
  @Autowired
  public SensorCollectionController (
    @NonNull final RestCollectionControllerConfiguration configuration
  )
  {
    super(configuration, Sensor.class);
  }

  @Override
  @Transactional
  public @NonNull Long post (
    @NonNull final JsonNode json
  )
  throws JsonProcessingException, InvalidModelException
  {
    @NonNull final Sensor sensor = getConfiguration().getObjectMapper().treeToValue(json, Sensor.class);

    getConfiguration().assertIsValid(sensor);
    getConfiguration().getApplicationEventPublisher().publishEvent(new ApplicationEntityEvent.Create(this, sensor));

    return sensor.getIdentifier();
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
