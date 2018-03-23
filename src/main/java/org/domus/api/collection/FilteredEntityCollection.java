package org.domus.api.collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.springframework.context.ApplicationContext;

import org.springframework.lang.NonNull;

import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.collection.specification.Specification;

public class FilteredEntityCollection<T> implements EntityCollection<T>
{
  /**
   * Type of entity in this collection.
   */
  @NonNull
  private final Class<T>         _entity;

  /**
   * Specification to apply to the full collection in order to filter results.
   */
  @NonNull
  private final Specification<T> _specification;

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
    @NonNull final Class<T> entity,
    @NonNull final Specification<T> specification,
    @NonNull final EntityManager entityManager
  )
  {
    this._entity = entity;
    this._specification = specification;
    this._entityManager = entityManager;
    this._criteriaBuilder = entityManager.getCriteriaBuilder();
  }

  public FilteredEntityCollection(
    @NonNull final Class<T> entity,
    @NonNull final Specification<T> specification,
    @NonNull final ApplicationContext context
  )
  {
    this._entity = entity;
    this._specification = specification;
    this._entityManager = context.getBean(EntityManager.class);
    this._criteriaBuilder = this._entityManager.getCriteriaBuilder();
  }

  /**
   * @see EntityCollection#getSize
   */
  public int getSize () {
    final EntityCollectionQuery<T, Long> criteriaQuery = this.createCollectionQuery(Long.class);
    criteriaQuery.select(_criteriaBuilder.count(criteriaQuery.getCollectionRoot()));
    return _entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
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
    return this._entityManager.createQuery(this.createCollectionQuery());
  }

  /**
   * @see EntityCollection#createCriteriaQuery
   */
  public EntityCollectionQuery<T, T> createCollectionQuery () {
    final EntityCollectionQuery<T, T> query = this.createCollectionQuery(this._entity);
    query.select(query.getCollectionRoot());
    return query;
  }

  /**
   * @see EntityCollection#createCriteriaQuery
   */
  public <U> EntityCollectionQuery<T, U> createCollectionQuery (@NonNull final Class<U> clazz) {
    final EntityCollectionQuery<T, U> result = new EntityCollectionQuery<>(
      this._criteriaBuilder.createQuery(clazz), _entity
    );
    result.where(this._specification.build(result.getCollectionRoot(), result, _criteriaBuilder));
    return result;
  }

  /**
   * @see EntityCollection#findById
   */
  public T findById (final int identifier) {
    final EntityCollectionQuery<T, T> query = this.createCollectionQuery();
    final Root<T> root = query.getCollectionRoot();
    query.where(
      this._criteriaBuilder.equal(root.get(root.getModel().getSingularAttribute("identifier", Integer.class)), identifier)
    );

    return this._entityManager.createQuery(query).getSingleResult();
  }

  /**
   * @see EntityCollection#findByIdOrFail
   */
  public T findByIdOrFail (final int identifier) throws EntityNotFoundException {
    final T value = this.findById(identifier);

    if (value == null) {
      throw new EntityNotFoundException();
    } else {
      return value;
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
    return this.createQuery().getResultList();
  }
}
