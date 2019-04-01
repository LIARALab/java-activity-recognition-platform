package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPACollections;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.cursoring.Cursor;
import org.liara.collection.operator.cursoring.CursorableCollection;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.ordering.Order;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public abstract class ComputedCollectionResource<Model extends ApplicationEntity>
  extends CollectionResource<Model>
{
  public ComputedCollectionResource (
    final @NonNull Class<Model> modelClass,
    final @NonNull CollectionResourceBuilder builder
  ) {
    super(modelClass, builder);
  }

  /**
   * Return the operator applied to the underlying collection.
   *
   * @return The operator applied to the underlying collection.
   */
  public abstract @NonNull Operator getOperator ();

  @Override
  public @NonNull ModelResource<Model> getFirstModelResource ()
  throws NoSuchElementException {
    @NonNull final Collection<Model> collection = Composition.of(
      Order.expression("identifier").ascending(),
      Cursor.FIRST
    ).apply(getCollection()).collectionOf(getModelClass());

    @NonNull final List<@NonNull Model> models = fetchCollection(collection);

    if (models.isEmpty()) {
      throw new NoSuchElementException("No first model found for this collection.");
    } else {
      return toModelResource(Objects.requireNonNull(models.get(0)));
    }
  }

  @Override
  public @NonNull ModelResource<Model> getLastModelResource ()
  throws NoSuchElementException {
    @NonNull final Collection<Model> collection = Composition.of(
      Order.expression("identifier").descending(),
      Cursor.FIRST
    ).apply(getCollection()).collectionOf(getModelClass());

    @NonNull final List<Model> models = fetchCollection(collection);

    if (models.isEmpty()) {
      throw new NoSuchElementException("No last model found for this collection.");
    } else {
      return toModelResource(Objects.requireNonNull(models.get(0)));
    }
  }

  @Override
  public @NonNull ModelResource<Model> getModelResource (@NonNull final UUID identifier)
  throws NoSuchElementException {
    @NonNull final Collection<Model> collection = Composition.of(
      Filter.expression(
        ":this.universalUniqueIdentifier = :identifier"
      ).setParameter("identifier", identifier.toString()),
      Cursor.FIRST
    ).apply(getCollection()).collectionOf(getModelClass());

    @NonNull final List<Model> models = fetchCollection(collection);

    if (models.isEmpty()) {
      throw new NoSuchElementException(
        "No model with uuid " + identifier.toString() + " found into this collection."
      );
    } else {
      return toModelResource(models.get(0));
    }
  }

  @Override
  public @NonNull ModelResource<Model> getModelResource (@NonNull final Long identifier)
  throws NoSuchElementException {
    @NonNull final Collection<Model> collection = Composition.of(
      Filter.expression(":this.identifier = :identifier").setParameter("identifier", identifier),
      Cursor.FIRST
    ).apply(getCollection()).collectionOf(getModelClass());

    @NonNull final List<Model> models = fetchCollection(collection);

    if (models.isEmpty()) {
      throw new NoSuchElementException(
        "No model with identifier " + identifier.toString() + " found into this collection."
      );
    } else {
      return toModelResource(models.get(0));
    }
  }

  private @NonNull List<@NonNull Model> fetchCollection (
    @NonNull final Collection<Model> collection
  ) {
    @NonNull final EntityManager entityManager = getEntityManagerFactory().createEntityManager();
    entityManager.getTransaction().begin();

    @NonNull final TypedQuery<Model> query = entityManager.createQuery(
      JPACollections.getQuery(collection, ":this").toString(),
      collection.getModelClass()
    );

    JPACollections.getParameters(collection).forEach(query::setParameter);

    if (collection instanceof CursorableCollection) {
      @NonNull final Cursor cursor = ((CursorableCollection<Model>) collection).getCursor();

      query.setFirstResult(cursor.getOffset());
      if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());
    }

    @NonNull final List<@NonNull Model> result = query.getResultList();

    entityManager.getTransaction().commit();
    entityManager.close();

    return result;
  }

  @Override
  public @NonNull Collection<Model> getCollection () {
    return getOperator().apply(super.getCollection()).collectionOf(getModelClass());
  }
}
