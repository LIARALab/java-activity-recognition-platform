package org.liara.api.metamodel.routing;

import org.checkerframework.checker.nullness.qual.NonNull;

public class InvalidMetamodelRoute
  implements MetamodelRoutingResult
{
  @NonNull
  private static InvalidMetamodelRoute INSTANCE = new InvalidMetamodelRoute();

  public static @NonNull InvalidMetamodelRoute getInstance () {
    return InvalidMetamodelRoute.INSTANCE;
  }

  public static boolean is (@NonNull final MetamodelRoutingResult result) {
    return result == InvalidMetamodelRoute.INSTANCE;
  }
}
