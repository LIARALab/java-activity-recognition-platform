package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPACollections;
import org.liara.collection.operator.Operator;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.metamodel.RestResource;
import org.liara.rest.request.RestRequest;
import org.liara.rest.request.handler.RestRequestHandler;
import org.liara.rest.response.RestResponse;
import reactor.core.publisher.Mono;

public class SQLResource
  implements RestResource
{
  @NonNull
  private final CollectionResource<?> _resource;

  protected SQLResource (@NonNull final CollectionResource<?> resource) {
    _resource = resource;
  }

  @Override
  public @NonNull Mono<RestResponse> get (
    @NonNull final RestRequest request
  )
  throws IllegalRestRequestException {
    @NonNull final RestRequestHandler configuration = _resource.getRequestConfiguration();

    try {
      configuration.validate(request.getParameters()).assertRequestIsValid();
      @NonNull final Operator      operator   = configuration.parse(request.getParameters());
      @NonNull final Collection<?> collection = operator.apply(_resource.getCollection());

      return Mono.just(RestResponse.ofType(String.class).ofModel(
        JPACollections.getQuery(collection, ":this").toString()
      ));
    } catch (@NonNull final InvalidAPIRequestException exception) {
      throw new IllegalRestRequestException(exception);
    }
  }
}
