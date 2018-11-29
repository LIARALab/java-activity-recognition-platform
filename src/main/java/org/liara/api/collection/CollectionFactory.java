package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.collection.jpa.JPAEntityCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionFactory
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final EntityBasedCollectionConfigurationFactory _entityBasedCollectionConfigurationFactory;

  @Autowired
  public CollectionFactory (
    @NonNull final EntityManager entityManager,
    @NonNull final EntityBasedCollectionConfigurationFactory entityBasedCollectionConfigurationFactory
  )
  {
    _entityManager = entityManager;
    _entityBasedCollectionConfigurationFactory = entityBasedCollectionConfigurationFactory;
  }

  public <Entity> @NonNull RequestConfiguration getConfiguration (
    @NonNull final Class<Entity> entity
  )
  {
    return _entityBasedCollectionConfigurationFactory.create(entity);
  }

  public <Entity> @NonNull JPAEntityCollection<Entity> getCollection (
    @NonNull final Class<Entity> entity
  )
  {
    return new JPAEntityCollection<>(_entityManager, entity);
  }

  public <Entity> @NonNull Entity getEntity (
    @NonNull final Class<Entity> entity, @NonNull final Long identifier
  )
  {
    @Nullable final Entity result = _entityManager.find(entity, identifier);

    if (result == null) {
      throw new EntityNotFoundException("Entity " + entity.getName() + "@" + identifier + " not found.");
    } else {
      return result;
    }
  }
}
