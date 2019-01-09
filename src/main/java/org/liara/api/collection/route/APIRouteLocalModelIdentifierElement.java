package org.liara.api.collection.route;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.controller.ModelController;

public class APIRouteLocalModelIdentifierElement
  extends APIRouteModelIdentifierElement
{
  @NonNull
  private final Long _identifier;

  protected APIRouteLocalModelIdentifierElement (
    @NonNull final Long identifier,
    @NonNull final ModelController<?> modelController
  ) {
    super(identifier.toString(), modelController);
    _identifier = identifier;
  }

  public @NonNull Long getIdentifier () {
    return _identifier;
  }
}
