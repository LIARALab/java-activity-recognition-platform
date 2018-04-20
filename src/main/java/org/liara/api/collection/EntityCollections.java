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


import javax.persistence.EntityManager;

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
  public <Entity> EntityCollection<Entity> createCollection (@NonNull final Class<Entity> entity) {
    return new BaseEntityCollection<>(entity, _entityManager);
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
