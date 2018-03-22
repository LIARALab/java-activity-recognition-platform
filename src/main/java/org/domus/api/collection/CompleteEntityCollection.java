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

public class CompleteEntityCollection<T> implements EntityCollection<T> {
  /**
  * Type of entity in this collection.
  */
  @NonNull private final Class<T> _entity;

  /**
  * The CRUD repository of the entity for basic CRUD operations.
  */
  @NonNull private final CrudRepository<T, Integer> _repository;

  /**
  * Builder for the criteria query.
  */
  @NonNull private final CriteriaBuilder _criteriaBuilder;

  /**
  * Entity manager.
  */
  @NonNull private final EntityManager _entityManager;

  public CompleteEntityCollection (
    @NonNull final Class<T> entity,
    @NonNull final EntityManager entityManager,
    @NonNull final CrudRepository<T, Integer> repository
  ) {
    this._entity = entity;
    this._entityManager = entityManager;
    this._criteriaBuilder = entityManager.getCriteriaBuilder();
    this._repository = repository;
  }

  @SuppressWarnings("unchecked")
public CompleteEntityCollection (
    @NonNull final Class<T> entity,
    @NonNull final ApplicationContext context
  ) {
    this._entity = entity;
    this._entityManager = context.getBean(EntityManager.class);
    this._criteriaBuilder = this._entityManager.getCriteriaBuilder();

    final String[] beans = context.getBeanNamesForType(
      ResolvableType.forClassWithGenerics(
        CrudRepository.class,
        entity,
        Integer.class
      )
    );

    this._repository = (CrudRepository<T, Integer>) context.getBean(beans[0]);
  }

  /**
  * @see EntityCollection#getSize
  */
  public int getSize () {
    return (int) this._repository.count();
  }

  /**
  * @see EntityCollection#getView
  */
  public EntityCollectionView<T> getView (final int offset, final int size) {
    return new EntityCollectionView<>(this, offset, size);
  }

  /**
  * @see EntityCollection#createQuery
  */
  public TypedQuery<T> createQuery () {
    return this._entityManager.createQuery(this.createCriteriaQuery());
  }

  /**
  * @see EntityCollection#createCriteriaQuery
  */
  public CriteriaQuery<T> createCriteriaQuery () {
    final CriteriaQuery<T> query = this.createCriteriaQuery(this._entity);
    query.select(query.from(this._entity));
    return query;
  }

  /**
  * @see EntityCollection#createCriteriaQuery
  */
  public <U> CriteriaQuery<U> createCriteriaQuery (
    @NonNull final Class<U> clazz
  ) {
    final CriteriaQuery<U> query = this._criteriaBuilder.createQuery(clazz);
    return query;
  }

  /**
  * @see EntityCollection#findById
  */
  public T findById (final int identifier) {
    final Optional<T> result = this._repository.findById(identifier);
    return (result.isPresent()) ? null : result.get();
  }

  /**
  * @see EntityCollection#findByIdOrFail
  */
  public T findByIdOrFail (
    final int identifier
  ) throws EntityNotFoundException {
    final Optional<T> result = this._repository.findById(identifier);

    if (result.isPresent()) {
      return result.get();
    } else {
      throw new EntityNotFoundException();
    }
  }

  /**
  * @see EntityCollection#get
  */
  public T get (final int index) {
    return null;
  }

  /**
  * @see EntityCollection#getContent
  */
  public Iterable<T> getContent () {
    return this._repository.findAll();
  }
}
