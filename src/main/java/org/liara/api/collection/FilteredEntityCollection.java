/*******************************************************************************
 * Copyright (C) 2018 C�dric DEMONGIVERT <cedric.demongivert@gmail.com>
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

import org.liara.api.collection.filtering.ComposedEntityFilter;
import org.liara.api.collection.filtering.EntityFilter;
import org.liara.api.collection.ordering.ComposedOrdering;
import org.liara.api.collection.ordering.EmptyOrdering;
import org.liara.api.collection.ordering.Ordering;
import org.springframework.lang.NonNull;

/**
 * A sorted and filtered entity collection.
 *
 * @author C�dric DEMONGIVERT <cedric.demongivert@gmail.com>
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
  private final EntityFilter<Entity> _filter;

  /**
   * Entity manager.
   */
  @NonNull
  private final EntityManager    _entityManager;
  
  @NonNull
  private final Ordering<Entity> _ordering;

  public FilteredEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final EntityFilter<Entity> filter,
    @NonNull final EntityManager entityManager
  )
  {
    _entity = entity;
    _filter = filter;
    _entityManager = entityManager;
    _ordering = new EmptyOrdering<>();
  }
  
  public FilteredEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final EntityFilter<Entity> filter,
    @NonNull final EntityManager entityManager,
    @NonNull final Ordering<Entity> ordering
  )
  {
    _entity = entity;
    _filter = filter;
    _entityManager = entityManager;
    _ordering = ordering;
  }

  /**
   * @see org.liara.api.collection.EntityCollection#createCriteriaQuery(java.lang.Class)
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz) {
    final CriteriaBuilder criteriaBuilder = _entityManager.getCriteriaBuilder();
    final EntityCollectionQuery<Entity, U> result = new RootBasedEntityCollectionQuery<>(
        criteriaBuilder.createQuery(clazz), _entity
    );
    _ordering.order(criteriaBuilder, result);
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
  public EntityCollection<Entity, Identifier> filter (@NonNull final EntityFilter<Entity> filter) {
    return new FilteredEntityCollection<>(getEntityClass(), new ComposedEntityFilter<>(Arrays.asList(_filter, filter)), getEntityManager());
  }

  @Override
  public EntityCollection<Entity, Identifier> order (@NonNull final Ordering<Entity> ordering) {
    if (_ordering instanceof EmptyOrdering) {
      return new FilteredEntityCollection<>(_entity, _filter, _entityManager, ordering);
    } else {
      return new FilteredEntityCollection<>(
          _entity, 
          _filter,
          _entityManager,
          new ComposedOrdering<>(Arrays.asList(_ordering, ordering))
      );
    }
  }

  @Override
  public Ordering<Entity> getOrdering () {
    return _ordering;
  }
}
