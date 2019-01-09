package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CollectionControllerHandler<Model>
{
  @NonNull
  private final CollectionController<Model> _controller;

  public CollectionControllerHandler (@NonNull final CollectionController<Model> controller) {
    _controller = controller;
  }

  public boolean isSupportingIndexOperation () {
    return _controller instanceof IndexableCollectionController;
  }

  public @NonNull Mono<ServerResponse> index (@NonNull final ServerRequest request) {
    if (isSupportingIndexOperation()) {
      @NonNull final IndexableCollectionController operation = (IndexableCollectionController) _controller;

      return Mono.just(request)
               .map(ServerRequest::queryParams)
               .map(APIRequest::new)
               .flatMap((@NonNull final APIRequest apiRequest) -> {
                 try {
                   return Mono.just(operation.index(apiRequest));
                 } catch (@NonNull final Exception exception) {
                   return Mono.error(exception);
                 }
               }).flatMap(this::makeResponseFromIndexResult);
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
    return _controller instanceof GetableCollectionController;
  }

  public @NonNull Mono<ServerResponse> getWithLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      @NonNull final GetableCollectionController<?> operation = (GetableCollectionController<?>) _controller;

      return Mono.just(request.pathVariable("identifier"))
                 .map(Long::parseLong)
                 .flatMap((@NonNull final Long identifier) -> {
                   try {
                     return Mono.just(operation.get(identifier));
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 })
                 .flatMap(this::makeResponseFromGetResult);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> getWithUUID (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      @NonNull final GetableCollectionController<?> operation = (GetableCollectionController<?>) _controller;

      return Mono.just(request.pathVariable("identifier"))
                 .map(UUID::fromString)
                 .flatMap((@NonNull final UUID identifier) -> {
                   try {
                     return Mono.just(operation.get(identifier));
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 })
                 .flatMap(this::makeResponseFromGetResult);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> makeResponseFromGetResult (@NonNull final Object result) {
    final ServerResponse.@NonNull BodyBuilder builder;

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), Object.class);
  }

  public boolean isSupportingPostOperation () {
    return _controller instanceof PostableCollectionController;
  }

  public @NonNull Mono<ServerResponse> create (@NonNull final ServerRequest request) {
    if (isSupportingPostOperation()) {
      @NonNull final PostableCollectionController operation = (PostableCollectionController) _controller;

      return request.bodyToMono(JsonNode.class)
                    .map(json -> json == null ? NullNode.getInstance() : json)
                    .flatMap((@NonNull final JsonNode json) -> {
                      try {
                        return Mono.just(operation.post(json));
                      } catch (@NonNull final Exception exception) {
                        return Mono.error(exception);
                      }
                    })
                    .flatMap(
                      (@NonNull final Long identifier) ->
                        ServerResponse.created(request.uri().resolve("/" + identifier)).build()
                    );
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public boolean isSupportingPutOperation () {
    return _controller instanceof PutableCollectionController;
  }

  public @NonNull Mono<ServerResponse> putByLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingPutOperation()) {
      @NonNull final PutableCollectionController operation = (PutableCollectionController) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class)
                                                           .map(json -> json == null ? NullNode.getInstance() : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(Long::parseLong)
               .flatMap(
                 (@NonNull final Long identifier) -> {
                   try {
                     return Mono.just(operation.put(identifier, body.block()));
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 }
               )
               .flatMap(
                 (@NonNull final Long identifier) ->
                   ServerResponse.created(request.uri().resolve("/" + identifier)).build()
               );
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> putByUUID (@NonNull final ServerRequest request) {
    if (isSupportingPutOperation()) {
      @NonNull final PutableCollectionController operation = (PutableCollectionController) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(UUID::fromString)
               .flatMap(
                 (@NonNull final UUID identifier) -> {
                   try {
                     return Mono.just(operation.put(identifier, body.block()));
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 }
               ).flatMap(
                 (@NonNull final Long identifier) ->
                   ServerResponse.created(request.uri().resolve("/" + identifier)).build()
               );
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public boolean isSupportingUpdateOperation () {
    return _controller instanceof UpdatableCollectionController;
  }

  public @NonNull Mono<ServerResponse> updateByLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingUpdateOperation()) {
      @NonNull final UpdatableCollectionController operation = (UpdatableCollectionController) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(Long::parseLong)
               .flatMap(
                 (@NonNull final Long identifier) -> {
                   try {
                     return Mono.just(operation.update(identifier, body.block()));
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 }
               )
               .flatMap(
                 (@NonNull final Long identifier) ->
                   ServerResponse.created(request.uri().resolve("/" + identifier)).build()
               );
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> updateByUUID (@NonNull final ServerRequest request) {
    if (isSupportingUpdateOperation()) {
      @NonNull final UpdatableCollectionController operation = (UpdatableCollectionController) _controller;

      @NonNull final Mono<@NonNull JsonNode> body = request.bodyToMono(JsonNode.class).map(json -> json == null
                                                                                                   ?
                                                                                                   NullNode.getInstance()
                                                                                                   : json);

      return Mono.just(request.pathVariable("identifier"))
               .map(UUID::fromString)
               .flatMap(
                 (@NonNull final UUID identifier) -> {
                   try {
                     return Mono.just(operation.update(identifier, body.block()));
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 }
               )
               .flatMap(
                 (@NonNull final Long identifier) ->
                   ServerResponse.created(request.uri().resolve("/" + identifier)).build()
               );
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public boolean isSupportingDeletion () {
    return _controller instanceof DeletableCollectionController;
  }


  public @NonNull Mono<ServerResponse> deleteByLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingDeletion()) {
      @NonNull final DeletableCollectionController operation = (DeletableCollectionController) _controller;

      return Mono.just(request.pathVariable("identifier"))
               .map(Long::parseLong)
               .flatMap(
                 (@NonNull final Long identifier) -> {
                   try {
                     operation.delete(identifier);
                     return Mono.empty();
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 }
               )
               .flatMap(x -> ServerResponse.ok().build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  public @NonNull Mono<ServerResponse> deleteByUUID (@NonNull final ServerRequest request) {
    if (isSupportingDeletion()) {
      @NonNull final DeletableCollectionController operation = (DeletableCollectionController) _controller;

      return Mono.just(request.pathVariable("identifier"))
               .map(UUID::fromString)
               .flatMap(
                 (@NonNull final UUID identifier) -> {
                   try {
                     operation.delete(identifier);
                     return Mono.empty();
                   } catch (@NonNull final Exception exception) {
                     return Mono.error(exception);
                   }
                 }
               )
               .flatMap(x -> ServerResponse.ok().build());
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }
}
