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

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

import org.liara.api.collection.filtering.EntityCollectionFilter;
import org.liara.api.collection.filtering.EntityFieldFilter;
import org.springframework.lang.NonNull;

/**
 * A sorted and filtered entity collection.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * @param <Entity> Type of entity in the collection.
 * @param <Identifier> Identifier type used for indexing the given entity type.
 */
public class FilteredEntityCollection<Entity, Identifier> implements EntityCollection<Entity, Identifier>
{
  /**
   * Type of entity in this collection.
   */
  @NonNull
  private final Class<Entity>         _entity;

  /**
   * Filter to apply to the full collection in order to get results.
   */
  @NonNull
  private final EntityCollectionFilter<Entity> _filter;

  /**
   * Entity manager.
   */
  @NonNull
  private final EntityManager    _entityManager;

  public FilteredEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final EntityCollectionFilter<Entity> filter,
    @NonNull final EntityManager entityManager
  )
  {
    _entity = entity;
    _filter = filter;
    _entityManager = entityManager;
  }

  /**
   * @see org.liara.api.collection.EntityCollection#createCriteriaQuery(java.lang.Class)
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz) {
    final CriteriaBuilder criteriaBuilder = _entityManager.getCriteriaBuilder();
    final EntityCollectionQuery<Entity, U> result = new EntityCollectionQuery<>(
        criteriaBuilder.createQuery(clazz), _entity
    );
    _filter.filter(criteriaBuilder, result);
    return result;
  }

  @Override
  public EntityManager getEntityManager () {
    return _entityManager;
  }

  @Override
  public Class<Entity> getEntityClass () {
    return _entity;
  }

  @Override
  public <Field> EntityCollection<Entity, Identifier> filter (@NonNull final EntityFieldFilter<Entity, Field> filter) {
    return new FilteredEntityCollection<>(getEntityClass(), _filter.add(filter), getEntityManager());
  }

  @Override
  public EntityCollection<Entity, Identifier> filter (@NonNull final EntityCollectionFilter<Entity> filter) {
    return new FilteredEntityCollection<>(getEntityClass(), _filter.merge(filter), getEntityManager());
  }
}
