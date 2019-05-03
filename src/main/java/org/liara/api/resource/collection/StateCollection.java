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

import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorType;
import org.liara.api.data.entity.state.State;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.resource.CollectionResource;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.error.InvalidModelException;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class StateCollection
  extends CollectionResource<State>
{
  @NonNull
  private final APIEventPublisher _apiEventPublisher;

  @NonNull
  private final Validator _validator;

  @NonNull
  private final TransactionTemplate _transactionTemplate;

  @Autowired
  public StateCollection (
    @NonNull final StateCollectionBuilder builder
  ) {
    super(State.class, Objects.requireNonNull(builder.getCollectionResourceBuilder()));
    _apiEventPublisher = Objects.requireNonNull(builder.getAPIEventPublisher());
    _validator = Objects.requireNonNull(builder.getValidator());
    _transactionTemplate = Objects.requireNonNull(builder.getTransactionTemplate());
  }

  @Override
  public @NonNull Mono<RestResponse> post (@NonNull final RestRequest request) {
    return request.getBody(State.class)
             .flatMap(this::post);
  }

  private @NonNull Class<? extends State> getStateTypeFromSensorIdentifier (
    @NonNull final JsonNode jsonNode
  ) {
    if (jsonNode.has("sensorIdentifier")) {
      @NonNull final EntityManager entityManager = getEntityManagerFactory().createEntityManager();
      entityManager.getTransaction().begin();

      @NonNull final Optional<Sensor> sensor = Optional.ofNullable(
        entityManager.find(Sensor.class, jsonNode.get("sensorIdentifier").asLong())
      );

      entityManager.getTransaction().commit();
      entityManager.close();

      return sensor.map(Sensor::getTypeInstance)
               .map(SensorType::getEmittedStateClass)
               .orElseThrow();
    } else {
      throw new Error("No sensorIdentifier field found into the posted content.");
    }
  }

  public @NonNull Mono<RestResponse> post (@NonNull final State state) {
    try {
      assertIsValid(state);

      _transactionTemplate.execute(status -> this.tryToPost(state));

      return Mono.just(
        RestResponse.ofType(Long.class).ofModel(
          Objects.requireNonNull(state.getIdentifier())
        )
      );
    } catch (@NonNull final InvalidModelException exception) {
      exception.printStackTrace();
      return Mono.error(new IllegalRestRequestException(exception));
    }
  }

  private boolean tryToPost (@NonNull final State state) {
    try {
      _apiEventPublisher.create(state);
      return true;
    } catch (@NonNull final Throwable throwable) {
      throw new Error(
        "Unable to post a new state into the application database. (" +
        state.toString(),
        throwable
      );
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
}
