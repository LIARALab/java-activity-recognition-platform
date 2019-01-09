package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.collection.configuration.EntityConfigurationFactory;

import javax.persistence.EntityManager;
import java.util.Objects;

public class ReadableControllerConfiguration
{
  @NonNull
  private final EntityConfigurationFactory _entityConfigurationFactory;

  @NonNull
  private final EntityManager _entityManager;

  public ReadableControllerConfiguration (
    @NonNull final ReadableControllerConfigurationBuilder builder
  )
  {
    _entityConfigurationFactory = Objects.requireNonNull(builder.getEntityConfigurationFactory());
    _entityManager = Objects.requireNonNull(builder.getEntityManager());
  }

  public @NonNull EntityConfigurationFactory getEntityConfigurationFactory () {
    return _entityConfigurationFactory;
  }

  public @NonNull EntityManager getEntityManager () {
    return _entityManager;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_entityManager, _entityConfigurationFactory);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ReadableControllerConfiguration) {
      @NonNull final ReadableControllerConfiguration otherConfiguration = (ReadableControllerConfiguration) other;

      return Objects.equals(_entityConfigurationFactory, otherConfiguration.getEntityConfigurationFactory()) &&
             Objects.equals(_entityManager, otherConfiguration.getEntityManager());
    }

    return false;
  }
}
