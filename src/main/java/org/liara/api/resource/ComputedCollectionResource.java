package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.Collection;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.cursoring.Cursor;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.ordering.Order;

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
    ).apply(getCollection()).expectedToBeCollectionOf(getModelClass());

    @NonNull final List<Model> models = collection.fetch();

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
    ).apply(getCollection()).expectedToBeCollectionOf(getModelClass());

    @NonNull final List<Model> models = collection.fetch();

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
    ).apply(getCollection()).expectedToBeCollectionOf(getModelClass());

    @NonNull final List<Model> models = collection.fetch();

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
    ).apply(getCollection()).expectedToBeCollectionOf(getModelClass());

    @NonNull final List<Model> models = collection.fetch();

    if (models.isEmpty()) {
      throw new NoSuchElementException(
        "No model with identifier " + identifier.toString() + " found into this collection."
      );
    } else {
      return toModelResource(models.get(0));
    }
  }

  @Override
  public @NonNull Collection<Model> getCollection () {
    return getOperator().apply(super.getCollection()).expectedToBeCollectionOf(getModelClass());
  }
}
