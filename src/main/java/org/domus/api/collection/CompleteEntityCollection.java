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

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.context.ApplicationContext;

import org.springframework.lang.NonNull;

import org.springframework.core.ResolvableType;

import org.springframework.data.repository.CrudRepository;

import org.domus.api.collection.exception.EntityNotFoundException;

/**
 * An unfiltered, complete, raw collection of entity.
 *       
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Type of entity in the collection.
 */
public class CompleteEntityCollection<Entity> implements EntityCollection<Entity>
{
  /**
   * Type of entity in this collection.
   */
  @NonNull
  private final Class<Entity>                   _entity;

  /**
   * The CRUD repository of the entity for basic CRUD operations.
   */
  @NonNull
  private final CrudRepository<Entity, Integer> _repository;

  /**
   * Builder for the criteria query.
   */
  @NonNull
  private final CriteriaBuilder                 _criteriaBuilder;

  /**
   * Entity manager.
   */
  @NonNull
  private final EntityManager                   _entityManager;

  public CompleteEntityCollection(
    @NonNull final Class<Entity> entity,
    @NonNull final EntityManager entityManager,
    @NonNull final CrudRepository<Entity, Integer> repository
  )
  {
    this._entity = entity;
    this._entityManager = entityManager;
    this._criteriaBuilder = entityManager.getCriteriaBuilder();
    this._repository = repository;
  }

  @SuppressWarnings("unchecked")
  public CompleteEntityCollection(@NonNull final Class<Entity> entity, @NonNull final ApplicationContext context) {
    this._entity = entity;
    this._entityManager = context.getBean(EntityManager.class);
    this._criteriaBuilder = this._entityManager.getCriteriaBuilder();

    final String[] beans = context
      .getBeanNamesForType(ResolvableType.forClassWithGenerics(CrudRepository.class, entity, Integer.class));

    this._repository = (CrudRepository<Entity, Integer>) context.getBean(beans[0]);
  }

  /**
   * @see org.domus.api.collection.EntityCollection#getSize()
   */
  public int getSize () {
    return (int) this._repository.count();
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
    return this._entityManager.createQuery(this.createCollectionQuery());
  }

  /**
   * @see org.domus.api.collection.EntityCollection#createCriteriaQuery()
   */
  public EntityCollectionQuery<Entity, Entity> createCollectionQuery () {
    final EntityCollectionQuery<Entity, Entity> query = this.createCollectionQuery(this._entity);
    query.select(query.getCollectionRoot());
    return query;
  }

  /**
   * @see org.domus.api.collection.EntityCollection#createCriteriaQuery(java.lang.Class)
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz) {
    final CriteriaQuery<U> query = this._criteriaBuilder.createQuery(clazz);
    return new EntityCollectionQuery<Entity, U>(query, this._entity);
  }

  /**
   * @see org.domus.api.collection.EntityCollection#findById(int)
   */
  public Entity findById (final int identifier) {
    final Optional<Entity> result = this._repository.findById(identifier);
    return (result.isPresent()) ? null : result.get();
  }

  /**
   * @see org.domus.api.collection.EntityCollection#findByIdOrFail(int)
   */
  public Entity findByIdOrFail (final int identifier) throws EntityNotFoundException {
    final Optional<Entity> result = this._repository.findById(identifier);

    if (result.isPresent()) {
      return result.get();
    } else {
      throw new EntityNotFoundException();
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
    return this._repository.findAll();
  }
}
