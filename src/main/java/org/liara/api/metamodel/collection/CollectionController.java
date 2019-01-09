package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @param <Model>
 */
public interface CollectionController<Model>
{
  /**
   * Return type of model stored into the described collection.
   *
   * @return The type of model stored into the described collection.
   */
  @NonNull Class<Model> getModelClass ();
}
