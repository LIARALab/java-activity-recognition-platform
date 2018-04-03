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
package org.domus.api.collection;


import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.lang.NonNull;

/**
 * An unfiltered, complete, raw collection of entity.
 *       
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Type of entity in the collection.
 * @param <Identifier> Identifier type used for indexing the given entity type.
 */
public class CompleteEntityCollection<Entity, Identifier> implements EntityCollection<Entity, Identifier>
{
  /**
   * Type of entity in this collection.
   */
  @NonNull
  private final Class<Entity>                   _entity;

  /**
   * Entity manager.
   */
  @NonNull
  private final EntityManager                   _entityManager;

  public CompleteEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final EntityManager entityManager
  )
  {
    this._entity = entity;
    this._entityManager = entityManager;
  }

  /**
   * @see org.domus.api.collection.EntityCollection#createCriteriaQuery(java.lang.Class)
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz) {
    final CriteriaBuilder criteriaBuilder = _entityManager.getCriteriaBuilder();
    final CriteriaQuery<U> query = criteriaBuilder.createQuery(clazz);
    
    return new EntityCollectionQuery<Entity, U>(query, _entity);
  }

  @Override
  public EntityManager getEntityManager () {
    return _entityManager;
  }

  @Override
  public Class<Entity> getEntityClass () {
    return _entity;
  }
}
