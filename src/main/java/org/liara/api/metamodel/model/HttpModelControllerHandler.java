package org.liara.api.metamodel.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.PutResult;
import org.liara.api.utils.JsonUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HttpModelControllerHandler<Model>
{
  @NonNull
  private final ModelController<Model> _controller;

  /**
   * Wrap a model controller into a handler.
   *
   * @param controller A controller to wrap.
   */
  public HttpModelControllerHandler (@NonNull final ModelController<Model> controller) {
    _controller = controller;
  }

  /**
   * Return true if the underlying model allows to perform a get request on it.
   *
   * @return True if the underlying model allows to perform a get request on it.
   */
  public boolean isSupportingGetOperation () {
    return _controller instanceof GetModelOperation;
  }

  /**
   * Perform a get request on the underlying model.
   *
   * @param identifier Identifier of the model to get.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> get (@NonNull final Long identifier) {
    return Mono.just(identifier).flatMap(this::doGet).flatMap(ServerResponse.ok()::syncBody);
  }

  /**
   * Perform a get request on the underlying model.
   *
   * @param identifier The identifier to request.
   *
   * @return The resulting collection.
   */
  private @NonNull Mono<Model> doGet (@NonNull final Long identifier) {
    if (isSupportingGetOperation()) {
      @NonNull final GetModelOperation<Model> operation = (GetModelOperation<Model>) _controller;
      try {
        return Mono.just(operation.get(identifier));
      } catch (@NonNull final EntityNotFoundException exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.GET, getSupportedMethods()));
    }
  }

  /**
   * Perform a get request on the underlying model.
   *
   * @param identifier Identifier of the model to get.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> get (@NonNull final UUID identifier) {
    return Mono.just(identifier).flatMap(this::doGet).flatMap(ServerResponse.ok()::syncBody);
  }

  /**
   * Perform a get request on the underlying model.
   *
   * @param identifier The identifier to request.
   *
   * @return The resulting collection.
   */
  private @NonNull Mono<Model> doGet (@NonNull final UUID identifier) {
    if (isSupportingGetOperation()) {
      @NonNull final GetModelOperation<Model> operation = (GetModelOperation<Model>) _controller;
      try {
        return Mono.just(operation.get(identifier));
      } catch (@NonNull final EntityNotFoundException exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.GET, getSupportedMethods()));
    }
  }

  /**
   * Return true if the underlying model allows to perform a put request on it.
   *
   * @return True if the underlying model allows to perform a put request on it.
   */
  public boolean isSupportingPutOperation () {
    return _controller instanceof PutModelOperation;
  }

  /**
   * Perform a put request on the underlying model.
   *
   * @param identifier Identifier of the model to put.
   * @param request    The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> put (@NonNull final Long identifier, @NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class).map(JsonUtils::mapToNullNodeIfNull).flatMap(json -> doPut(
      identifier,
      json
    )).flatMap(result -> result.toModelServerResponse(request));
  }

  /**
   * Perform a put request on the underlying model.
   *
   * @param identifier Identifier of the model to put.
   * @param json       The json content to put.
   *
   * @return The result of the put operation.
   */
  private @NonNull Mono<@NonNull PutResult> doPut (@NonNull final Long identifier, @NonNull final JsonNode json) {
    if (isSupportingPutOperation()) {
      @NonNull final PutModelOperation operation = (PutModelOperation) _controller;

      try {
        return Mono.just(operation.put(identifier, json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.PUT, getSupportedMethods()));
    }
  }

  /**
   * Perform a put request on the underlying model.
   *
   * @param identifier Identifier of the model to put.
   * @param request    The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> put (@NonNull final UUID identifier, @NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class).map(JsonUtils::mapToNullNodeIfNull).flatMap(json -> doPut(
      identifier,
      json
    )).flatMap(result -> result.toModelServerResponse(request));
  }

  /**
   * Perform a put request on the underlying model.
   *
   * @param identifier Identifier of the model to put.
   * @param json       The json content to put.
   *
   * @return The result of the put operation.
   */
  private @NonNull Mono<@NonNull PutResult> doPut (@NonNull final UUID identifier, @NonNull final JsonNode json) {
    if (isSupportingPutOperation()) {
      @NonNull final PutModelOperation operation = (PutModelOperation) _controller;

      try {
        return Mono.just(operation.put(identifier, json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.PUT, getSupportedMethods()));
    }
  }

  /**
   * Return true if the underlying model allows to perform a patch request on it.
   *
   * @return True if the underlying model allows to perform a patch request on it.
   */
  public boolean isSupportingPatchOperation () {
    return _controller instanceof PatchModelOperation;
  }

  /**
   * Perform a patch request on the underlying model.
   *
   * @param identifier Identifier of the model to patch.
   * @param request    The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> patch (@NonNull final Long identifier, @NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class).map(JsonUtils::mapToNullNodeIfNull).flatMap(json -> doPatch(
      identifier,
      json
    ));
  }

  /**
   * Perform a patch request on the underlying model.
   *
   * @param json The json content to put.
   *
   * @return The server response to send.
   */
  private @NonNull Mono<ServerResponse> doPatch (@NonNull final Long identifier, @NonNull final JsonNode json) {
    if (isSupportingPatchOperation()) {
      @NonNull final PatchModelOperation operation = (PatchModelOperation) _controller;

      try {
        operation.patch(identifier, json);
        return ServerResponse.ok().build();
      } catch (@NonNull final Throwable exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.PATCH, getSupportedMethods()));
    }
  }

  /**
   * Perform a patch request on the underlying model.
   *
   * @param identifier Identifier of the model to patch.
   * @param request    The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> patch (@NonNull final UUID identifier, @NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class).map(JsonUtils::mapToNullNodeIfNull).flatMap(json -> doPatch(
      identifier,
      json
    ));
  }

  /**
   * Perform a patch request on the underlying model.
   *
   * @param json The json content to put.
   *
   * @return The server response to send.
   */
  private @NonNull Mono<ServerResponse> doPatch (@NonNull final UUID identifier, @NonNull final JsonNode json) {
    if (isSupportingPatchOperation()) {
      @NonNull final PatchModelOperation operation = (PatchModelOperation) _controller;

      try {
        operation.patch(identifier, json);
        return ServerResponse.ok().build();
      } catch (@NonNull final Throwable exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.PATCH, getSupportedMethods()));
    }
  }

  /**
   * Return true if the underlying model allows to perform a patch request on it.
   *
   * @return True if the underlying model allows to perform a patch request on it.
   */
  public boolean isSupportingDeleteOperation () {
    return _controller instanceof DeleteModelOperation;
  }

  /**
   * Perform a delete request on the underlying model.
   *
   * @param identifier Identifier of the model to delete.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> delete (@NonNull final Long identifier) {
    return Mono.just(identifier).flatMap(this::doDelete).flatMap(x -> ServerResponse.ok().build());
  }

  /**
   * Perform a delete request on the underlying model.
   *
   * @param identifier Identifier of the model to delete.
   *
   * @return The number of deleted elements.
   */
  public @NonNull Mono<Void> doDelete (@NonNull final Long identifier) {
    if (isSupportingDeleteOperation()) {
      @NonNull final DeleteModelOperation operation = (DeleteModelOperation) _controller;

      try {
        operation.delete(identifier);
        return Mono.empty();
      } catch (@NonNull final EntityNotFoundException exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.DELETE, getSupportedMethods()));
    }
  }

  /**
   * Perform a delete request on the underlying model.
   *
   * @param identifier Identifier of the model to delete.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> delete (@NonNull final UUID identifier) {
    return Mono.just(identifier).flatMap(this::doDelete).flatMap(x -> ServerResponse.ok().build());
  }

  /**
   * Perform a delete request on the underlying model.
   *
   * @param identifier Identifier of the model to delete.
   *
   * @return The number of deleted elements.
   */
  public @NonNull Mono<Void> doDelete (@NonNull final UUID identifier) {
    if (isSupportingDeleteOperation()) {
      @NonNull final DeleteModelOperation operation = (DeleteModelOperation) _controller;

      try {
        operation.delete(identifier);
        return Mono.empty();
      } catch (@NonNull final EntityNotFoundException exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.DELETE, getSupportedMethods()));
    }
  }

  /**
   * Return a set of http methods supported by this collection.
   *
   * @return A set of http methods supported by this collection.
   */
  public @NonNull Set<@NonNull HttpMethod> getSupportedMethods () {
    @NonNull final Set<@NonNull HttpMethod> result = new HashSet<>();

    if (isSupportingGetOperation()) result.add(HttpMethod.GET);
    if (isSupportingPutOperation()) result.add(HttpMethod.PUT);
    if (isSupportingPatchOperation()) result.add(HttpMethod.PATCH);
    if (isSupportingDeleteOperation()) result.add(HttpMethod.DELETE);

    return result;
  }

  /**
   * Return the underlying model controller.
   *
   * @return The underlying model controller.
   */
  public @NonNull ModelController<Model> getController () {
    return _controller;
  }
}
