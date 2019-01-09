package org.liara.api.metamodel.routing;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.model.HttpModelControllerHandlerFactory;
import org.liara.api.metamodel.model.ModelController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class LongModelMetamodelRoute
  extends ModelMetamodelRoute
{
  @NonNull
  private final Long _identifier;

  public LongModelMetamodelRoute (
    @NonNull final String pathElement, @NonNull final ModelController<?> modelController, @NonNull final Long identifier
  )
  {
    super(pathElement, modelController);
    _identifier = identifier;
  }

  public @NonNull Long getIdentifier () {
    return _identifier;
  }

  @Override
  public @NonNull Mono<ServerResponse> get (@NonNull final ServerRequest request) {
    return HttpModelControllerHandlerFactory.getHandlerFor(getModelController()).get(_identifier);
  }

  @Override
  public @NonNull Mono<ServerResponse> patch (@NonNull final ServerRequest request) {
    return HttpModelControllerHandlerFactory.getHandlerFor(getModelController()).patch(_identifier, request);
  }

  @Override
  public @NonNull Mono<ServerResponse> put (@NonNull final ServerRequest request) {
    return HttpModelControllerHandlerFactory.getHandlerFor(getModelController()).put(_identifier, request);
  }

  @Override
  public @NonNull Mono<ServerResponse> delete (@NonNull final ServerRequest request) {
    return HttpModelControllerHandlerFactory.getHandlerFor(getModelController()).delete(_identifier);
  }
}
