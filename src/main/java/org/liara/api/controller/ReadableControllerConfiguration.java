package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.rest.request.jpa.EntityHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Primary
public class ReadableControllerConfiguration
{
  @NonNull
  private final EntityHandlerFactory _entityConfigurationFactory;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public ReadableControllerConfiguration (
    @NonNull final ReadableControllerConfigurationBuilder builder
  )
  {
    _entityConfigurationFactory = Objects.requireNonNull(builder.getEntityConfigurationFactory());
    _entityManager = Objects.requireNonNull(builder.getEntityManager());
  }

  public @NonNull EntityHandlerFactory getEntityConfigurationFactory () {
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

      return Objects.equals(_entityConfigurationFactory, otherConfiguration.getEntityConfigurationFactory()) && Objects.equals(_entityManager, otherConfiguration.getEntityManager());
    }

    return false;
  }
}
