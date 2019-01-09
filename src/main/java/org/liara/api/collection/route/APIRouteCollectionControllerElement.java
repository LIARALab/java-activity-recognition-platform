package org.liara.api.collection.route;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.collection.CollectionController;

public class APIRouteCollectionControllerElement
  extends BaseAPIRouteElement
{
  @NonNull
  private final CollectionController<?> _collectionController;

  protected APIRouteCollectionControllerElement (
    @NonNull final String token,
    @NonNull final CollectionController<?> collectionController
  ) {
    super(token);
    _collectionController = collectionController;
  }

  public @NonNull CollectionController<?> getCollectionController () {
    return _collectionController;
  }
}
