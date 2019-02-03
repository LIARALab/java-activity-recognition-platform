package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.rest.request.jpa.EntityFilteringHandlerFactory;
import org.liara.rest.request.jpa.EntityOrderingHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class CollectionResourceBuilder
{
  @Nullable
  private EntityFilteringHandlerFactory _entityFilteringHandlerFactory;

  @Nullable
  private EntityOrderingHandlerFactory _entityOrderingHandlerFactory;

  @Nullable
  private EntityManager _entityManager;

  public CollectionResourceBuilder () {
    _entityFilteringHandlerFactory = null;
    _entityOrderingHandlerFactory = null;
    _entityManager = null;
  }

  public <Model extends ApplicationEntity> @NonNull CollectionResource<Model> build (
    @NonNull final Class<Model> modelClass
  ) {
    return new CollectionResource<>(modelClass, this);
  }

  public @Nullable EntityFilteringHandlerFactory getEntityFilteringHandlerFactory () {
    return _entityFilteringHandlerFactory;
  }

  @Autowired
  public @NonNull CollectionResourceBuilder setEntityFilteringHandlerFactory (
    @Nullable final EntityFilteringHandlerFactory entityFilteringHandlerFactory
  ) {
    _entityFilteringHandlerFactory = entityFilteringHandlerFactory;
    return this;
  }

  public @Nullable EntityOrderingHandlerFactory getEntityOrderingHandlerFactory () {
    return _entityOrderingHandlerFactory;
  }

  @Autowired
  public @NonNull CollectionResourceBuilder setEntityOrderingHandlerFactory (
    @Nullable final EntityOrderingHandlerFactory entityOrderingHandlerFactory
  ) {
    _entityOrderingHandlerFactory = entityOrderingHandlerFactory;
    return this;
  }

  public @Nullable EntityManager getEntityManager () {
    return _entityManager;
  }

  @Autowired
  public @NonNull CollectionResourceBuilder setEntityManager (
    @Nullable final EntityManager entityManager
  ) {
    _entityManager = entityManager;
    return this;
  }

  public @NonNull Class<CollectionResource> getOutputClass () {
    return CollectionResource.class;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof CollectionResourceBuilder) {
      @NonNull final CollectionResourceBuilder otherCollectionResourceBuilder =
        (CollectionResourceBuilder) other;

      return Objects.equals(
        _entityFilteringHandlerFactory,
        otherCollectionResourceBuilder.getEntityFilteringHandlerFactory()
      ) &&
             Objects.equals(
               _entityOrderingHandlerFactory,
               otherCollectionResourceBuilder.getEntityOrderingHandlerFactory()
             ) &&
             Objects.equals(
               _entityManager,
               otherCollectionResourceBuilder.getEntityManager()
             );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _entityFilteringHandlerFactory,
      _entityOrderingHandlerFactory,
      _entityManager
    );
  }
}
