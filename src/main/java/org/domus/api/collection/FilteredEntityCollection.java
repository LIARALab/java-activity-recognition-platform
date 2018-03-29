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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.springframework.context.ApplicationContext;

import org.springframework.lang.NonNull;

import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.filter.Filter;

/**
 * A sorted and filtered entity collection.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * @param <Entity> Type of entity stored in this collection.
 */
public class FilteredEntityCollection<Entity> implements EntityCollection<Entity>
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
  private final Filter<Entity> _filter;

  /**
   * Builder for the criteria query.
   */
  @NonNull
  private final CriteriaBuilder  _criteriaBuilder;

  /**
   * Entity manager.
   */
  @NonNull
  private final EntityManager    _entityManager;

  public FilteredEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final Filter<Entity> filter,
    @NonNull final EntityManager entityManager
  )
  {
    _entity = entity;
    _filter = filter;
    _entityManager = entityManager;
    _criteriaBuilder = entityManager.getCriteriaBuilder();
  }

  public FilteredEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final Filter<Entity> filter,
    @NonNull final ApplicationContext context
  )
  {
    _entity = entity;
    _filter = filter;
    _entityManager = context.getBean(EntityManager.class);
    _criteriaBuilder = _entityManager.getCriteriaBuilder();
  }

  /**
   * @see org.domus.api.collection.EntityCollection#getSize()
   */
  public int getSize () {
    final EntityCollectionQuery<Entity, Long> criteriaQuery = this.createCollectionQuery(Long.class);
    criteriaQuery.select(_criteriaBuilder.count(criteriaQuery.getCollectionRoot()));
    return _entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
  }

  /**
   * @see org.domus.api.collection.EntityCollection#getView(org.domus.api.collection.Cursor)
   */
  public EntityCollectionView<Entity> getView (@NonNull final Cursor cursor) {
    return new EntityCollectionView<>(this, cursor);
  }

  /**
   * @see org.domus.api.collection.EntityCollection#createQuery()
   */
  public TypedQuery<Entity> createQuery () {
    return _entityManager.createQuery(this.createCollectionQuery());
  }

  /**
   * @see org.domus.api.collection.EntityCollection#createCriteriaQuery()
   */
  public EntityCollectionQuery<Entity, Entity> createCollectionQuery () {
    final EntityCollectionQuery<Entity, Entity> query = this.createCollectionQuery(_entity);
    query.select(query.getCollectionRoot());
    return query;
  }

  /**
   * @see org.domus.api.collection.EntityCollection#createCriteriaQuery(java.lang.Class)
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz) {
    final EntityCollectionQuery<Entity, U> result = new EntityCollectionQuery<>(
      _criteriaBuilder.createQuery(clazz), _entity
    );
    result.where(_filter.toPredicate(result.getCollectionRoot(), result, _criteriaBuilder));
    return result;
  }

  /**
   * @see org.domus.api.collection.EntityCollection#findById(int)
   */
  public Entity findById (final int identifier) {
    final EntityCollectionQuery<Entity, Entity> query = this.createCollectionQuery();
    final Root<Entity> root = query.getCollectionRoot();
    query.where(_criteriaBuilder.equal(root.get("identifier"), identifier));

    return _entityManager.createQuery(query).getSingleResult();
  }

  /**
   * @see org.domus.api.collection.EntityCollection#findByIdOrFail(int)
   */
  public Entity findByIdOrFail (final int identifier) throws EntityNotFoundException {
    final Entity value = this.findById(identifier);

    if (value == null) {
      throw new EntityNotFoundException();
    } else {
      return value;
    }
  }

  /**
   * @see org.domus.api.collection.EntityCollection#get(int)
   */
  public Entity get (final int index) {
    return null;
  }

  /**
   * @see org.domus.api.collection.EntityCollection#getContent()
   */
  public Iterable<Entity> getContent () {
    return this.createQuery().getResultList();
  }
}
