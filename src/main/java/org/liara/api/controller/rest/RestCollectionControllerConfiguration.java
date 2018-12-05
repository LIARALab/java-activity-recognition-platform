package org.liara.api.controller.rest;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.collection.configuration.EntityConfigurationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

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

  public RestCollectionControllerConfiguration () {
    _entityManager = null;
    _entityConfigurationFactory = null;
    _applicationEventPublisher = null;
  }

  public RestCollectionControllerConfiguration (@NonNull final RestCollectionControllerConfiguration toCopy) {
    _entityManager = toCopy.getEntityManager();
    _entityConfigurationFactory = toCopy.getEntityConfigurationFactory();
    _applicationEventPublisher = toCopy.getApplicationEventPublisher();
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
             Objects.equals(_entityManager, otherConfiguration.getEntityManager());
    }

    return false;
  }
}
