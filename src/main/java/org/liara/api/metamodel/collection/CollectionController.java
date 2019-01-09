package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.model.ModelController;

/**
 * A collection of database models.
 *
 * @param <Model> Database model stored into this collection.
 */
public interface CollectionController<Model>
{
  /**
   * Return the collection's model controller.
   *
   * @return The collection's model controller.
   */
  @NonNull ModelController<Model> getModelController ();

  /**
   * Return type of database model stored into the described collection.
   *
   * @return The type of database model stored into the described collection.
   */
  @NonNull Class<Model> getModelClass ();
}
