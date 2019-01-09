package org.liara.api.collection.route;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.controller.ModelController;

public class APIRouteModelIdentifierElement
  extends BaseAPIRouteElement
{
  @NonNull
  private final ModelController<?> _modelController;

  protected APIRouteModelIdentifierElement (
    @NonNull final String token,
    @NonNull final ModelController<?> modelController
  ) {
    super(token);
    _modelController = modelController;
  }

  public @NonNull ModelController<?> getModelController () {
    return _modelController;
  }
}
