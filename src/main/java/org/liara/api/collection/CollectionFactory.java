package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.jpa.JPAEntityCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionFactory
{
  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public CollectionFactory (@NonNull final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public <Entity> @NonNull CollectionRequestConfiguration<Entity> getConfiguration (@NonNull final Class<Entity> entity) {
    return new EntityBasedCollectionConfiguration<>(_entityManager, entity);
  }

  public <Entity> @NonNull JPAEntityCollection<Entity> getCollection (@NonNull final Class<Entity> entity) {
    return new JPAEntityCollection<>(_entityManager, entity);
  }

  public <Entity> @NonNull Entity getEntity (@NonNull final Class<Entity> entity, @NonNull final Long identifier) {
    return _entityManager.find(entity, identifier);
  }
}
