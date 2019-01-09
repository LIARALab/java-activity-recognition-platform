package org.liara.api.metamodel.routing;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.metamodel.collection.CollectionController;

import java.util.Objects;

public class CollectionMetamodelRoute
  implements MetamodelRoutingResult
{
  @NonNull
  private final String _pathElement;

  @NonNull
  private final CollectionController<?> _collectionController;

  public CollectionMetamodelRoute (
    @NonNull final String pathElement, @NonNull final CollectionController<?> collectionController
  )
  {
    _pathElement = pathElement;
    _collectionController = collectionController;
  }

  public static boolean is (@NonNull final MetamodelRoutingResult result) {
    return result instanceof CollectionMetamodelRoute;
  }

  public @NonNull String getPathElement () {
    return _pathElement;
  }

  public @NonNull CollectionController<?> getCollectionController () {
    return _collectionController;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (this == other) return true;

    if (other instanceof CollectionMetamodelRoute) {
      @NonNull final CollectionMetamodelRoute otherRoute = (CollectionMetamodelRoute) other;

      return Objects.equals(_collectionController, otherRoute.getCollectionController()) && Objects.equals(
        _pathElement,
        otherRoute.getPathElement()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_pathElement, _collectionController);
  }
}
