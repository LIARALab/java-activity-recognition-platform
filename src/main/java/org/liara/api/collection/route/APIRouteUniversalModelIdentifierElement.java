package org.liara.api.collection.route;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.controller.ModelController;

import java.util.UUID;

public class APIRouteUniversalModelIdentifierElement
  extends APIRouteModelIdentifierElement
{
  @NonNull
  private final UUID _identifier;

  protected APIRouteUniversalModelIdentifierElement (
    @NonNull final UUID identifier,
    @NonNull final ModelController<?> modelController
  ) {
    super(identifier.toString(), modelController);
    _identifier = identifier;
  }

  public @NonNull UUID getIdentifier () {
    return _identifier;
  }
}
