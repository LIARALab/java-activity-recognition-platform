package org.domus.api.collection;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.lang.NonNull;

import org.domus.api.collection.exception.EntityNotFoundException;

public interface EntityCollection<T> {
  /**
  * Return the size of the collection.
  *
  * @return The size of the collection.
  */
  public int getSize ();

  /**
  * Return a view of this collection.
  *
  * @param offset The index of the first element to display in the returned view.
  * @param size The number of elements to display in the returned view.
  *
  * @return A view of this collection.
  */
  public EntityCollectionView<T> getView (final int offset, final int size);

  /**
  * Return a view of this collection.
  *
  * By default, the returned view will begin with the first element of this
  * collection.
  *
  * @param size The number of elements to display in the returned view.
  *
  * @return A view of this collection.
  */
  public default EntityCollectionView<T> getView (final int size) {
    return this.getView(0, size);
  }

  /**
  * Return a view over all this collection.
  *
  * @return A view of this collection.
  */
  public default EntityCollectionView<T> getView () {
    return this.getView(0, this.getSize());
  }

  /**
  * Return the content of this collection.
  *
  * @return The content of this collection.
  */
  public default Iterable<T> getContent () {
    return this.createQuery().getResultList();
  }

  /**
  * Try to find an entity of the collection.
  *
  * @param identifier Identifier of the entity to get.
  */
  public T findById (final int identifier);

  /**
  * Try to find an entity of the collection.
  *
  * @param identifier Identifier of the entity to get.
  */
  public T findByIdOrFail (final int identifier) throws EntityNotFoundException;

  /**
  * Return an entity of the collection.
  *
  * @param index Position of the entity to return in this collection.
  */
  public T get (final int index);

  /**
  * Return a query over this collection.
  *
  * @return A query over this collection.
  */
  public TypedQuery<T> createQuery ();

  /**
  * Return a criteria query over this collection.
  *
  * A criteria query can be muted.
  *
  * @return A criteria query over this collection.
  */
  public CriteriaQuery<T> createCriteriaQuery ();

  /**
  * Return a criteria query over this collection with a custom return type.
  *
  * A criteria query can be muted.
  *
  * @param clazz The custom return type of the query.
  *
  * @return A criteria query over this collection with a custom return type.
  */
  public <U> CriteriaQuery<U> createCriteriaQuery (
    @NonNull final Class<U> clazz
  );
}
