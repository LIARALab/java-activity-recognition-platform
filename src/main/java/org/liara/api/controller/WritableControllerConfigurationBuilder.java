package org.liara.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.rest.request.jpa.EntityHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.validation.Validator;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WritableControllerConfigurationBuilder
  extends ReadableControllerConfigurationBuilder
{
  @Nullable
  private ApplicationEventPublisher _applicationEventPublisher;

  @Nullable
  private ObjectMapper _objectMapper;

  @Nullable
  private Validator _validator;

  public @Nullable ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  @Autowired
  public @NonNull WritableControllerConfigurationBuilder setApplicationEventPublisher (
    @Nullable final ApplicationEventPublisher applicationEventPublisher
  )
  {
    _applicationEventPublisher = applicationEventPublisher;
    return this;
  }

  @Override
  public @NonNull WritableControllerConfiguration build () {
    return new WritableControllerConfiguration(this);
  }

  public @Nullable ObjectMapper getObjectMapper () {
    return _objectMapper;
  }

  @Autowired
  public @NonNull WritableControllerConfigurationBuilder setObjectMapper (
    @Nullable final ObjectMapper objectMapper
  )
  {
    _objectMapper = objectMapper;
    return this;
  }

  public @Nullable Validator getValidator () {
    return _validator;
  }

  @Autowired
  public @NonNull WritableControllerConfigurationBuilder setValidator (
    @Nullable final Validator validator
  )
  {
    _validator = validator;
    return this;
  }

  @Override
  @Autowired
  public @NonNull WritableControllerConfigurationBuilder setEntityManager (
    @Nullable final EntityManager entityManager
  )
  {
    super.setEntityManager(entityManager);
    return this;
  }

  @Override
  @Autowired
  public @NonNull WritableControllerConfigurationBuilder setEntityConfigurationFactory (
    @Nullable final EntityHandlerFactory entityConfigurationFactory
  )
  {
    super.setEntityConfigurationFactory(entityConfigurationFactory);
    return this;
  }

  @Override
  public int hashCode () {
    return Objects.hash(super.hashCode(), _applicationEventPublisher, _objectMapper, _validator);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (!super.equals(other)) return false;

    if (other instanceof WritableControllerConfigurationBuilder) {
      @NonNull final WritableControllerConfigurationBuilder otherBuilder =
        (WritableControllerConfigurationBuilder) other;

      return Objects.equals(_applicationEventPublisher, otherBuilder.getApplicationEventPublisher()) && Objects.equals(
        _objectMapper,
        otherBuilder.getObjectMapper()
      ) && Objects.equals(_validator, otherBuilder.getValidator());
    }

    return false;
  }
}
