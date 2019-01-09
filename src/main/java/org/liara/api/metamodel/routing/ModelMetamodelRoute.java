package org.liara.api.metamodel.routing;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.metamodel.model.ModelController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

public abstract class ModelMetamodelRoute
  implements MetamodelRoutingResult
{
  @NonNull
  private final String _pathElement;

  @NonNull
  private final ModelController<?> _modelController;

  public ModelMetamodelRoute (
    @NonNull final String pathElement, @NonNull final ModelController<?> modelController
  )
  {
    _pathElement = pathElement;
    _modelController = modelController;
  }

  public static boolean is (@NonNull final MetamodelRoutingResult result) {
    return result instanceof ModelMetamodelRoute;
  }

  public @NonNull ModelController<?> getModelController () {
    return _modelController;
  }

  public @NonNull String getPathElement () {
    return _pathElement;
  }

  public abstract @NonNull Mono<ServerResponse> get (@NonNull final ServerRequest request);

  public abstract @NonNull Mono<ServerResponse> patch (@NonNull final ServerRequest request);

  public abstract @NonNull Mono<ServerResponse> put (@NonNull final ServerRequest request);

  public abstract @NonNull Mono<ServerResponse> delete (@NonNull final ServerRequest request);

  @Override
  public int hashCode () {
    return Objects.hash(_pathElement, _modelController);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ModelMetamodelRoute) {
      @NonNull final ModelMetamodelRoute otherRoute = (ModelMetamodelRoute) other;

      return Objects.equals(_modelController, otherRoute.getModelController()) && Objects.equals(
        _pathElement,
        otherRoute.getPathElement()
      );
    }

    return false;
  }
}
