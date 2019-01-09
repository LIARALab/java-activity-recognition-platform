package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.collection.configuration.EntityConfigurationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ReadableControllerConfigurationBuilder
{
  @Nullable
  private EntityConfigurationFactory _entityConfigurationFactory;

  @Nullable
  private EntityManager _entityManager;

  public ReadableControllerConfigurationBuilder () {
    _entityConfigurationFactory = null;
    _entityManager = null;
  }

  @Bean
  public @NonNull ReadableControllerConfiguration build () {
    return new ReadableControllerConfiguration(this);
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

  public @Nullable EntityConfigurationFactory getEntityConfigurationFactory () {
    return _entityConfigurationFactory;
  }

  @Autowired
  public @NonNull ReadableControllerConfigurationBuilder setEntityConfigurationFactory (
    @Nullable final EntityConfigurationFactory entityConfigurationFactory
  )
  {
    _entityConfigurationFactory = entityConfigurationFactory;
    return this;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_entityConfigurationFactory, _entityManager);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ReadableControllerConfigurationBuilder) {
      @NonNull final ReadableControllerConfigurationBuilder otherBuilder =
        (ReadableControllerConfigurationBuilder) other;

      return Objects.equals(_entityConfigurationFactory, otherBuilder.getEntityConfigurationFactory()) &&
             Objects.equals(_entityManager, otherBuilder.getEntityManager());
    }

    return false;
  }
}
