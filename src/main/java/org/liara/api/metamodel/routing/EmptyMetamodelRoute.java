package org.liara.api.metamodel.routing;

import org.checkerframework.checker.nullness.qual.NonNull;

public class EmptyMetamodelRoute
  implements MetamodelRoutingResult
{
  @NonNull
  private static EmptyMetamodelRoute INSTANCE = new EmptyMetamodelRoute();

  public static @NonNull EmptyMetamodelRoute getInstance () {
    return EmptyMetamodelRoute.INSTANCE;
  }

  public static boolean is (@NonNull final MetamodelRoutingResult result) {
    return result == EmptyMetamodelRoute.INSTANCE;
  }
}
