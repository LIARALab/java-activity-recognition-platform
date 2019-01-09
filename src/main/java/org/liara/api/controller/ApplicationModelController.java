package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.controller.ModelController;

public class ApplicationModelController<Model> implements ModelController<Model>
{
  @NonNull
  private final Class<Model> _modelClass;

  protected ApplicationModelController (@NonNull final Class<Model> modelClass) {
    _modelClass = modelClass;
  }

  @Override
  public @NonNull Class<Model> getModelClass () {
    return _modelClass;
  }
}
