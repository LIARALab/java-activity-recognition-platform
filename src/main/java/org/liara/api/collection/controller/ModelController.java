package org.liara.api.collection.controller;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ModelController<Model>
{
  /**
   * Return type of model managed by this controller.
   *
   * @return The type of model managed by this controller.
   */
  @NonNull Class<Model> getModelClass ();
}
