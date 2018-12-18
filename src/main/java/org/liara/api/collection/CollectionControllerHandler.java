package org.liara.api.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public class CollectionControllerHandler
{
  @NonNull
  private final Object _controller;

  private CollectionControllerHandler (@NonNull final Object controller) {
    _controller = controller;
  }

  public static @NonNull CollectionControllerHandler instanciate (@NonNull final Object controller) {
    if (CollectionControllers.isCollectionController(controller)) {
      return new CollectionControllerHandler(controller);
    } else {
      throw new Error(
        "Unable to instanciate a collection controller handler for " + controller.toString() + " because " +
        controller.toString() + " is not annotated as a " + CollectionController.class.toString());
    }
  }

  public @NonNull RouterFunction<ServerResponse> createRouterFunction () {
    return RouterFunctions.route(RequestPredicates.method(HttpMethod.GET), this::index).and(RouterFunctions.route(RequestPredicates.GET("/{identifier:[0-9]+}"),
      this::getWithLongIdentifier
    )).and(RouterFunctions.route(RequestPredicates.GET("/{identifier}"), this::getWithUUID)).and(RouterFunctions.route(RequestPredicates.method(HttpMethod.POST),
      this::create
    ));
  }

  public boolean isSupportingIndexOperation () {
    return _controller instanceof CollectionOperation.Index;
  }

  public @NonNull Mono<ServerResponse> index (@NonNull final ServerRequest request) {
    if (isSupportingIndexOperation()) {
      return tryToIndex(request);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> tryToIndex (@NonNull final ServerRequest request) {
    try {
      final CollectionOperation.@NonNull Index<?> operation = (CollectionOperation.Index<?>) _controller;
      @NonNull final JPAEntityCollection<?>       result    = operation.index(new APIRequest(request.queryParams()));
      final ServerResponse.@NonNull BodyBuilder   builder;

      if (!result.getCursor().hasLimit() || result.getCursor().getLimit() > result.findSize()) {
        builder = ServerResponse.status(HttpStatus.OK);
      } else {
        builder = ServerResponse.status(HttpStatus.PARTIAL_CONTENT);
      }

      return builder.contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), Collection.class);
    } catch (@NonNull final InvalidAPIRequestException exception) {
      return ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(Mono.just(
        exception), InvalidAPIRequestException.class);
    }
  }

  public boolean isSupportingGetOperation () {
    return _controller instanceof CollectionOperation.Get;
  }

  public @NonNull Mono<ServerResponse> getWithLongIdentifier (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      return tryToGetWithLongIdentifier(request);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> tryToGetWithLongIdentifier (@NonNull final ServerRequest request) {
    try {
      final CollectionOperation.@NonNull Get<?> operation = (CollectionOperation.Get<?>) _controller;
      @NonNull final Object                     result    = operation.get(Long.parseLong(request.pathVariable(
        "identifier")));

      return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), Object.class);
    } catch (@NonNull final EntityNotFoundException exception) {
      return ServerResponse.notFound().build();
    }
  }

  public @NonNull Mono<ServerResponse> getWithUUID (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      return tryToGetWithUUID(request);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> tryToGetWithUUID (@NonNull final ServerRequest request) {
    try {
      final CollectionOperation.@NonNull Get<?> operation = (CollectionOperation.Get<?>) _controller;
      @NonNull final Object                     result    = operation.get(UUID.fromString(request.pathVariable(
        "identifier")));

      return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), Object.class);
    } catch (@NonNull final EntityNotFoundException exception) {
      return ServerResponse.notFound().build();
    } catch (@NonNull final IllegalArgumentException exception) {
      return ServerResponse.badRequest().build();
    }
  }

  public boolean isSupportingCreationOperation () {
    return _controller instanceof CollectionOperation.Create;
  }

  public @NonNull Mono<ServerResponse> create (@NonNull final ServerRequest request) {
    if (isSupportingGetOperation()) {
      return tryToCreate(request);
    } else {
      return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
  }

  private @NonNull Mono<ServerResponse> tryToCreate (@NonNull final ServerRequest request) {
    try {
      final CollectionOperation.@NonNull Create operation = (CollectionOperation.Create) _controller;

      request.bodyToMono(JsonNode.class)
        .map(json -> json == null ? NullNode.getInstance() : json)
        .map(operation::create)
        .onErrorResume(x -> Mono.just(2L));

      return ServerResponse.created(request.uri().resolve("/" + identifier)).build();
    } catch (@NonNull final EntityNotFoundException exception) {
      return ServerResponse.notFound().build();
    } catch (@NonNull final JsonProcessingException exception) {
      return ServerResponse.unprocessableEntity().body(Mono.just(exception), JsonProcessingException.class);
    } catch (@NonNull final InvalidRequestBodyException exception) {
      return ServerResponse.unprocessableEntity().body(Mono.just(exception), InvalidRequestBodyException.class);
    }
  }
}
