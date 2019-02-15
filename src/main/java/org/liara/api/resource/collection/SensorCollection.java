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
package org.liara.api.resource.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.resource.CollectionResource;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.error.InvalidModelException;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SensorCollection
  extends CollectionResource<Sensor>
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @NonNull
  private final Validator _validator;

  @Autowired
  public SensorCollection (@NonNull final SensorCollectionBuilder builder) {
    super(Sensor.class, Objects.requireNonNull(builder.getCollectionResourceBuilder()));

    _applicationEventPublisher = Objects.requireNonNull(builder.getApplicationEventPublisher());
    _validator = Objects.requireNonNull(builder.getValidator());
  }

  @Override
  public @NonNull Mono<RestResponse> post (@NonNull final RestRequest request) {
    return request.getBody(Sensor.class).flatMap(this::post);
  }

  public @NonNull Mono<RestResponse> post (@NonNull final Sensor sensor) {
    try {
      assertIsValid(sensor);
      _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, sensor));

      return Mono.just(RestResponse.ofType(Long.class).ofModel(
        Objects.requireNonNull(sensor.getIdentifier()))
      );
    } catch (@NonNull final InvalidModelException exception) {
      return Mono.error(new IllegalRestRequestException(exception));
    }
  }

  public void assertIsValid (@NonNull final Object object)
  throws InvalidModelException {
    @NonNull final Set<@NonNull ConstraintViolation<@NonNull Object>> errors = _validator.validate(
      object);

    if (errors.size() > 0) {
      throw new InvalidModelException(errors);
    }
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
