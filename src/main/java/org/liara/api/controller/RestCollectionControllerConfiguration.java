package org.liara.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.collection.InvalidRequestBodyException;
import org.liara.api.collection.configuration.EntityConfigurationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RestCollectionControllerConfiguration
{
  @Nullable
  private EntityManager _entityManager;

  @Nullable
  private EntityConfigurationFactory _entityConfigurationFactory;

  @Nullable
  private ApplicationEventPublisher _applicationEventPublisher;

  @Nullable
  private ObjectMapper _objectMapper;

  @Nullable
  private Validator _validator;

  public RestCollectionControllerConfiguration () {
    _entityManager = null;
    _entityConfigurationFactory = null;
    _applicationEventPublisher = null;
    _objectMapper = null;
    _validator = null;
  }

  public RestCollectionControllerConfiguration (@NonNull final RestCollectionControllerConfiguration toCopy) {
    _entityManager = toCopy.getEntityManager();
    _entityConfigurationFactory = toCopy.getEntityConfigurationFactory();
    _applicationEventPublisher = toCopy.getApplicationEventPublisher();
    _objectMapper = toCopy.getObjectMapper();
    _validator = toCopy.getValidator();
  }

  public void assertIsValid (@NonNull final Object object)
  throws InvalidRequestBodyException
  {
    @NonNull final Set<@NonNull ConstraintViolation<@NonNull Object>> errors = _validator.validate(object);

    if (errors.size() > 0) {
      throw new InvalidRequestBodyException(errors);
    }
  }

  public @Nullable Validator getValidator () {
    return _validator;
  }

  @Autowired
  public void setValidator (@Nullable final Validator validator) {
    _validator = validator;
  }

  public @Nullable ObjectMapper getObjectMapper () {
    return _objectMapper;
  }

  @Autowired
  public void setObjectMapper (@Nullable final ObjectMapper objectMapper) {
    _objectMapper = objectMapper;
  }

  public @Nullable EntityManager getEntityManager () {
    return _entityManager;
  }

  @Autowired
  public void setEntityManager (@Nullable final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public @Nullable EntityConfigurationFactory getEntityConfigurationFactory () {
    return _entityConfigurationFactory;
  }

  @Autowired
  public void setEntityConfigurationFactory (@Nullable final EntityConfigurationFactory entityConfigurationFactory) {
    _entityConfigurationFactory = entityConfigurationFactory;
  }

  public @Nullable ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  @Autowired
  public void setApplicationEventPublisher (@Nullable final ApplicationEventPublisher applicationEventPublisher) {
    _applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_applicationEventPublisher, _entityConfigurationFactory, _entityManager);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof RestCollectionControllerConfiguration) {
      @NonNull final RestCollectionControllerConfiguration otherConfiguration =
        (RestCollectionControllerConfiguration) other;

      return Objects.equals(_applicationEventPublisher, otherConfiguration.getApplicationEventPublisher()) &&
             Objects.equals(_entityConfigurationFactory, otherConfiguration.getEntityConfigurationFactory()) &&
             Objects.equals(_entityManager, otherConfiguration.getEntityManager()) && Objects.equals(
        _objectMapper,
        otherConfiguration.getObjectMapper()
      ) && Objects.equals(_validator, otherConfiguration.getValidator());
    }

    return false;
  }
}
