package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.rest.request.jpa.EntityHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Primary
public class ReadableControllerConfigurationBuilder
{
  @Nullable
  private EntityHandlerFactory _entityConfigurationFactory;

  @Nullable
  private EntityManager _entityManager;

  @Nullable
  private ApplicationContext _applicationContext;

  public ReadableControllerConfigurationBuilder () {
    _entityConfigurationFactory = null;
    _entityManager = null;
    _applicationContext = null;
  }

  public @NonNull ReadableControllerConfiguration build () {
    return new ReadableControllerConfiguration(this);
  }


  public @Nullable ApplicationContext getApplicationContext () {
    return _applicationContext;
  }

  @Autowired
  public @NonNull ReadableControllerConfigurationBuilder setApplicationContext (@Nullable final ApplicationContext applicationContext) {
    _applicationContext = applicationContext;
    return this;
  }

  public @Nullable EntityManager getEntityManager () {
    return _entityManager;
  }

  @Autowired
  public @NonNull ReadableControllerConfigurationBuilder setEntityManager (
    @Nullable final EntityManager entityManager
  )
  {
    _entityManager = entityManager;
    return this;
  }

  public @Nullable EntityHandlerFactory getEntityConfigurationFactory () {
    return _entityConfigurationFactory;
  }

  @Autowired
  public @NonNull ReadableControllerConfigurationBuilder setEntityConfigurationFactory (
    @Nullable final EntityHandlerFactory entityConfigurationFactory
  )
  {
    _entityConfigurationFactory = entityConfigurationFactory;
    return this;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_entityConfigurationFactory, _entityManager, _applicationContext);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ReadableControllerConfigurationBuilder) {
      @NonNull final ReadableControllerConfigurationBuilder otherBuilder =
        (ReadableControllerConfigurationBuilder) other;

      return Objects.equals(_entityConfigurationFactory, otherBuilder.getEntityConfigurationFactory()) &&
             Objects.equals(_entityManager, otherBuilder.getEntityManager()) && Objects.equals(
        _applicationContext,
        otherBuilder.getApplicationContext()
      );
    }

    return false;
  }
}
