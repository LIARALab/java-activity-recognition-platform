package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.relation.RelationManager;
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
  private RelationBasedFilteringHandlerFactory _entityFilteringHandlerFactory;

  @Nullable
  private RelationBasedOrderingProcessorFactory _entityOrderingHandlerFactory;

  @Nullable
  private EntityManager _entityManager;

  @Nullable
  private RelationManager _relationManager;

  public CollectionResourceBuilder () {
    _entityFilteringHandlerFactory = null;
    _entityOrderingHandlerFactory = null;
    _entityManager = null;
    _relationManager = null;
  }

  public CollectionResourceBuilder (@NonNull final CollectionResourceBuilder toCopy) {
    _entityFilteringHandlerFactory = toCopy.getEntityFilteringHandlerFactory();
    _entityOrderingHandlerFactory = toCopy.getEntityOrderingHandlerFactory();
    _entityManager = toCopy.getEntityManager();
    _relationManager = toCopy.getRelationManager();
  }

  public <Model extends ApplicationEntity> @NonNull CollectionResource<Model> build (
    @NonNull final Class<Model> modelClass
  ) {
    return new CollectionResource<>(modelClass, this);
  }

  public @NonNull RelationManager getRelationManager () {
    return _relationManager;
  }

  @Autowired
  public void setRelationManager (@NonNull final RelationManager relationManager) {
    _relationManager = relationManager;
  }

  public @Nullable RelationBasedFilteringHandlerFactory getEntityFilteringHandlerFactory () {
    return _entityFilteringHandlerFactory;
  }

  @Autowired
  public @NonNull CollectionResourceBuilder setEntityFilteringHandlerFactory (
    @Nullable final RelationBasedFilteringHandlerFactory entityFilteringHandlerFactory
  ) {
    _entityFilteringHandlerFactory = entityFilteringHandlerFactory;
    return this;
  }

  public @Nullable RelationBasedOrderingProcessorFactory getEntityOrderingHandlerFactory () {
    return _entityOrderingHandlerFactory;
  }

  @Autowired
  public @NonNull CollectionResourceBuilder setEntityOrderingHandlerFactory (
    @Nullable final RelationBasedOrderingProcessorFactory entityOrderingHandlerFactory
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
             ) &&
             Objects.equals(
               _relationManager,
               otherCollectionResourceBuilder.getRelationManager()
             );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _entityFilteringHandlerFactory,
      _entityOrderingHandlerFactory,
      _entityManager,
      _relationManager
    );
  }
}
