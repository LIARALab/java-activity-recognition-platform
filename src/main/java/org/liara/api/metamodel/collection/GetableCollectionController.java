package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.controller.ModelController;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface GetableCollectionController<Model> extends CollectionController<Model>
{
  /**
   * Return a specific model of the collection by using its local identifier.
   *
   * @param identifier The identifier of the model to get.
   *
   * @return The model of this collection with the given identifier.
   *
   * @throws EntityNotFoundException If the requested model does not exists.
   */
  @NonNull Model get (@NonNull final Long identifier) throws EntityNotFoundException;

  /**
   * Return a specific model of the collection by using its universal identifier.
   *
   * @param uuid The identifier of the model to get.
   *
   * @return The model of this collection with the given identifier.
   *
   * @throws EntityNotFoundException If the requested model does not exists.
   */
  @NonNull Model get (@NonNull final UUID uuid) throws EntityNotFoundException;

  /**
   * Return the collection's model controller.
   *
   * @return The collection model controller.
   */
  @NonNull ModelController<Model> getModelController ();
}
