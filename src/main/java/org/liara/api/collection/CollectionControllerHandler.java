package org.liara.api.collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CollectionControllerHandler
{
  @NonNull
  private final Object _controller;

  private CollectionControllerHandler (@NonNull final Object controller) {
    _controller = controller;
  }

  public static @NonNull CollectionControllerHandler instantiate (@NonNull final Object controller) {
    CollectionControllers.assertThatIsACollectionController(controller,
      "Unable to instantiate a collection controller handler for " + controller.toString()
    );

    return new CollectionControllerHandler(controller);
  }

  public @NonNull RouterFunction<ServerResponse> createRouterFunction () {
    return RouterFunctions.nest(RequestPredicates.method(HttpMethod.GET), RouterFunctions.route(RequestPredicates.path(
      "/"), this::index)
                                                                            .andRoute(
                                                                              RequestPredicates.path(
                                                                                "/{identifier:[0-9]+}"),
                                                                              this::getWithLongIdentifier
                                                                            )
                                                                            .andRoute(RequestPredicates.path(
                                                                              "/{identifier}"), this::getWithUUID))
             .andRoute(RequestPredicates.POST("/"), this::create)
             .andNest(RequestPredicates.method(HttpMethod.PUT),
               RouterFunctions.route(RequestPredicates.path("/{identifier:[0-9]+}"), this::setByLongIdentifier)
                 .andRoute(RequestPredicates.path("/{identifier}"), this::setByUUID)
             )
             .andNest(RequestPredicates.method(HttpMethod.PATCH),
               RouterFunctions.route(RequestPredicates.path("/{identifier:[0-9]+}"), this::mutateByLongIdentifier)
                 .andRoute(RequestPredicates.path("/{identifier}"), this::mutateByUUID)
             )
             .andNest(RequestPredicates.method(HttpMethod.DELETE),
               RouterFunctions.route(RequestPredicates.path("/{identifier:[0-9]+}"), this::deleteByLongIdentifier)
                 .andRoute(RequestPredicates.path("/{identifier}"), this::deleteByUUID)
             );
  }

  public boolean isSupportingIndexOperation () {
    return _controller instanceof CollectionOperation.Index;
  }

  public @NonNull Mono<ServerResponse> index (@NonNull final ServerRequest request) {
    if (isSupportingIndexOperation()) {
      final CollectionOperation.@NonNull Index<?> operation = (CollectionOperation.Index<?>) _controller;

      return Mono.just(request)
               .map(ServerRequest::queryParams)
               .map(APIRequest::new)
               .flatMap(operation::indexOrFail)
               .flatMap(this::makeResponseFromIndexResult);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> makeResponseFromIndexResult (@NonNull final JPAEntityCollection<?> result) {
    final ServerResponse.@NonNull BodyBuilder builder;

    if (!result.getCursor().hasLimit() || result.getCursor().getLimit() > result.findSize()) {
      builder = ServerResponse.status(HttpStatus.OK);
    } else {
      builder = ServerResponse.status(HttpStatus.PARTIAL_CONTENT);
    }

    return builder.contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), Collection.class);
  }

  public boolean isSupportingGetOperation () {
    return _controller instanceof CollectionOperation.Get;
  }

  public @NonNull Mono<ServerResponse> getWithLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      final CollectionOperation.@NonNull Get<?> operation = (CollectionOperation.Get<?>) _controller;

      return Mono.just(request.pathVariable("identifier")).map(Long::parseLong).flatMap(operation::getOrFail).flatMap(
        this::makeResponseFromGetResult);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> getWithUUID (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      final CollectionOperation.@NonNull Get<?> operation = (CollectionOperation.Get<?>) _controller;

      return Mono.just(request.pathVariable("identifier")).map(UUID::fromString).flatMap(operation::getOrFail).flatMap(
        this::makeResponseFromGetResult);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> makeResponseFromGetResult (@NonNull final Object result) {
    final ServerResponse.@NonNull BodyBuilder builder;

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), Object.class);
  }

  public boolean isSupportingCreationOperation () {
    return _controller instanceof CollectionOperation.Create;
  }

  public @NonNull Mono<ServerResponse> create (@NonNull final ServerRequest request) {
    if (isSupportingCreationOperation()) {
      final CollectionOperation.@NonNull Create operation = (CollectionOperation.Create) _controller;

      return request.bodyToMono(JsonNode.class).map(json -> json == null ? NullNode.getInstance() : json).flatMap(
        operation::createOrFail).flatMap((@NonNull final Long identifier) -> ServerResponse.created(request.uri()
                                                                                                      .resolve("/" +
                                                                                                               identifier))
                                                                               .build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public boolean isSupportingSetOperation () {
    return _controller instanceof CollectionOperation.Set;
  }

  public @NonNull Mono<ServerResponse> setByLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingSetOperation()) {
      final CollectionOperation.@NonNull Set operation = (CollectionOperation.Set) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(Long::parseLong)
               .flatMap((@NonNull final Long identifier) -> operation.setOrFail(identifier, body.block()))
               .flatMap((@NonNull final Long identifier) -> ServerResponse.created(request.uri()
                                                                                     .resolve("/" + identifier))
                                                              .build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> setByUUID (@NonNull final ServerRequest request) {
    if (isSupportingSetOperation()) {
      final CollectionOperation.@NonNull Set operation = (CollectionOperation.Set) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(UUID::fromString)
               .flatMap((@NonNull final UUID identifier) -> operation.setOrFail(identifier, body.block()))
               .flatMap((@NonNull final Long identifier) -> ServerResponse.created(request.uri()
                                                                                     .resolve("/" + identifier))
                                                              .build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public boolean isSupportingMutationOperation () {
    return _controller instanceof CollectionOperation.Mutate;
  }

  public @NonNull Mono<ServerResponse> mutateByLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingMutationOperation()) {
      final CollectionOperation.@NonNull Mutate operation = (CollectionOperation.Mutate) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(Long::parseLong)
               .flatMap((@NonNull final Long identifier) -> operation.mutateOrFail(identifier, body.block()))
               .flatMap((@NonNull final Long identifier) -> ServerResponse.created(request.uri()
                                                                                     .resolve("/" + identifier))
                                                              .build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> mutateByUUID (@NonNull final ServerRequest request) {
    if (isSupportingMutationOperation()) {
      final CollectionOperation.@NonNull Mutate operation = (CollectionOperation.Mutate) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(UUID::fromString)
               .flatMap((@NonNull final UUID identifier) -> operation.mutateOrFail(identifier, body.block()))
               .flatMap((@NonNull final Long identifier) -> ServerResponse.created(request.uri()
                                                                                     .resolve("/" + identifier))
                                                              .build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public boolean isSupportingDeletion () {
    return _controller instanceof CollectionOperation.Mutate;
  }


  public @NonNull Mono<ServerResponse> deleteByLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingDeletion()) {
      final CollectionOperation.@NonNull Delete operation = (CollectionOperation.Delete) _controller;

      return Mono.just(request.pathVariable("identifier"))
               .map(Long::parseLong)
               .flatMap(operation::deleteOrFail)
               .flatMap(x -> ServerResponse.ok().build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> deleteByUUID (@NonNull final ServerRequest request) {
    if (isSupportingDeletion()) {
      final CollectionOperation.@NonNull Delete operation = (CollectionOperation.Delete) _controller;

      return Mono.just(request.pathVariable("identifier"))
               .map(UUID::fromString)
               .flatMap(operation::deleteOrFail)
               .flatMap(x -> ServerResponse.ok().build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }
}
