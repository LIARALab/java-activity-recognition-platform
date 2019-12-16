package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.rest.metamodel.RestResource;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import reactor.core.publisher.Mono;

public class ModelResource<Model extends ApplicationEntity>
  implements RestResource
{
  @NonNull
  private final Class<Model> _modelClass;

  @NonNull
  private final Model _model;

  protected ModelResource (
    @NonNull final Class<Model> modelClass,
    @NonNull final Model model
  ) {
    _modelClass = modelClass;
    _model = model;
  }

  public @NonNull Model getModel () {
    return _model;
  }

  public @NonNull Class<Model> getModelClass () {
    return _modelClass;
  }

  @Override
  public @NonNull Mono<RestResponse> get (@NonNull final RestRequest request) {
    return Mono.just(RestResponse.ofType(_modelClass).ofModel(_model));
  }
}
