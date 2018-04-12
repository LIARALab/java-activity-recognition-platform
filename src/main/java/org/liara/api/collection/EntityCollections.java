/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.collection;

import org.springframework.lang.NonNull;

import java.util.List;

import javax.persistence.EntityManager;

import org.liara.api.collection.filtering.ComposedEntityFilter;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.RootBasedEntityCollectionQuery;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

/**
 * A service that offer many helpers for creating and managing entities collections.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
@Component
public class EntityCollections
{
  @NonNull
  private final EntityManager _entityManager;

  /**
   * Create a new EntityCollection service.
   * 
   * @param context Application context attached to the service. (Dependency injection)
   * @param entityManager EntityManager attached to the service. (Query management)
   */
  @Autowired
  public EntityCollections(
    @NonNull final EntityManager entityManager
  ) {
    this._entityManager = entityManager;
  }

  /**
   * Create and return a complete entity collection. (A collection with all entities in it).
   * 
   * @param entity Entity type of the collection to return.
   * @return A complete entity collection.
   */
  public <Entity> EntityCollection<Entity, Long> createCollection (@NonNull final Class<Entity> entity) {
    return new CompleteEntityCollection<>(entity, _entityManager);
  }

  /**
   * Create and return a filtered entity collection.
   *
   * @param entity Entity type of the collection to return.
   * @param specification Specification that all entity must match in order to be in the filtered collection.
   * @return A filtered entity collection.
   */
  public <Entity> EntityCollection<Entity, Long> createCollection (
    @NonNull final Class<Entity> entity, 
    @NonNull final ComposedEntityFilter<Entity> filter
  ) {
    return new FilteredEntityCollection<Entity, Long>(entity, filter, _entityManager);
  }
  
  /**
   * Create and return a raw entity collection query.
   * 
   * @param entity Type of entity targeted by the returned query.
   * 
   * @return A raw entity collection query.
   */
  public <Entity> EntityCollectionQuery<Entity, Entity> createQuery (
    @NonNull final Class<Entity> entity
  ) {
    return new RootBasedEntityCollectionQuery<>(
        _entityManager.getCriteriaBuilder().createQuery(entity), 
        entity
    );
  }
  
  /**
   * Create and return a raw entity collection query.
   * 
   * @param entity Type of entity targeted by the returned query.
   * 
   * @return A raw entity collection query.
   */
  public <Entity, ReturnType> EntityCollectionQuery<Entity, ReturnType> createQuery (
    @NonNull final Class<Entity> entity,
    @NonNull final Class<ReturnType> returnType
  ) {
    return new RootBasedEntityCollectionQuery<>(
        _entityManager.getCriteriaBuilder().createQuery(returnType), 
        entity
    );
  }
  
  /**
   * Search all results of the given entity collection query into the application database and return them.
   * 
   * @param query A query to execute.
   * @return All results returned by the given entity collection query.
   */
  public <T> List<T> fetch (@NonNull final EntityCollectionQuery<?, T> query) {
    return this._entityManager.createQuery(query).getResultList();
  }
  
  /**
   * Search all results of the given entity collection query into the application database and return the first one.
   * 
   * @param query A query to execute.
   * @return The first result returned by the given entity collection query.
   */
  public <T> T first (@NonNull final EntityCollectionQuery<?, T> query) {
    return this._entityManager.createQuery(query).getSingleResult();
  }
  
  /**
   * Return the entity manager attached to this service.
   * 
   * The entity manager allow this service to manager database queries for you.
   * 
   * @see EntityManager
   * 
   * @return The entity manager attached to this service.
   */
  public EntityManager getEntityManager () {
    return this._entityManager;
  }
}
