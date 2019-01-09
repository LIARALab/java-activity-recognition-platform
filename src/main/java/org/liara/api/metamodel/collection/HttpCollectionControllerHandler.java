package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.PutResult;
import org.liara.api.utils.JsonUtils;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HttpCollectionControllerHandler<Model>
{
  @NonNull
  private final CollectionController<Model> _controller;

  /**
   * Wrap a collection controller into a handler.
   *
   * @param controller A controller to wrap.
   */
  public HttpCollectionControllerHandler (@NonNull final CollectionController<Model> controller) {
    _controller = controller;
  }

  /**
   * Return true if the underlying collection allows to perform a get request on it.
   *
   * @return True if the underlying collection allows to perform a get request on it.
   */
  public boolean isSupportingGetOperation () {
    return _controller instanceof GetCollectionOperation;
  }

  /**
   * Perform a get request on the underlying collection.
   *
   * @param request The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> get (@NonNull final ServerRequest request) {
    return Mono.just(request)
               .map(ServerRequest::queryParams)
               .map(APIRequest::new).flatMap(this::doGet).flatMap(this::makeResponseFromGetResult);
  }

  /**
   * Perform a get request on the underlying collection.
   *
   * @param request The api request to apply.
   *
   * @return The resulting collection.
   */
  private @NonNull Mono<JPAEntityCollection<?>> doGet (@NonNull final APIRequest request) {
    if (isSupportingGetOperation()) {
      @NonNull final GetCollectionOperation<Model> operation = (GetCollectionOperation<Model>) _controller;
      try {
        return Mono.just(operation.get(request));
      } catch (@NonNull final InvalidAPIRequestException exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.GET, getSupportedMethods()));
    }
  }

  /**
   * Transform the result of a get operation on a collection into a server response.
   *
   * @param result The result to transform.
   *
   * @return The server response to send.
   */
  private @NonNull Mono<ServerResponse> makeResponseFromGetResult (@NonNull final JPAEntityCollection<?> result) {
    final ServerResponse.@NonNull BodyBuilder builder;

    if (!result.getCursor().hasLimit() || result.getCursor().getLimit() > result.findSize()) {
      builder = ServerResponse.status(HttpStatus.OK);
    } else {
      builder = ServerResponse.status(HttpStatus.PARTIAL_CONTENT);
    }

    return builder.contentType(MediaType.APPLICATION_JSON).body(Mono.just(result), JPAEntityCollection.class);
  }

  /**
   * Perform a count request on the underlying collection.
   *
   * @param request The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> count (@NonNull final ServerRequest request) {
    return Mono.just(request).map(ServerRequest::queryParams).map(APIRequest::new).flatMap(this::doCount).flatMap(
      ServerResponse.ok()::syncBody);
  }

  /**
   * Perform a get request on the underlying collection.
   *
   * @param request The api request to apply.
   *
   * @return The resulting collection.
   */
  private @NonNull Mono<@NonNull Long> doCount (@NonNull final APIRequest request) {
    if (isSupportingGetOperation()) {
      @NonNull final GetCollectionOperation<Model> operation = (GetCollectionOperation<Model>) _controller;
      try {
        return Mono.just(operation.get(request).findSize());
      } catch (@NonNull final InvalidAPIRequestException exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.GET, Collections.emptyList()));
    }
  }

  /**
   * Return true if the underlying collection allows to perform a doPost request on it.
   *
   * @return True if the underlying collection allows to perform a doPost request on it.
   */
  public boolean isSupportingPostOperation () {
    return _controller instanceof PostCollectionOperation;
  }

  /**
   * Perform a post request on the underlying collection.
   *
   * @param request The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> post (@NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class).map(JsonUtils::mapToNullNodeIfNull).flatMap(this::doPost).flatMap(
      identifier -> ServerResponse.created(request.uri().resolve("./" + identifier)).build());
  }

  /**
   * Perform a post request on the underlying collection.
   *
   * @param json The json content to post.
   *
   * @return The identifier of the added element.
   */
  private @NonNull Mono<Long> doPost (@NonNull final JsonNode json) {
    if (isSupportingPostOperation()) {
      @NonNull final PostCollectionOperation operation = (PostCollectionOperation) _controller;

      try {
        return Mono.just(operation.post(json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.POST, getSupportedMethods()));
    }
  }

  /**
   * Return true if the underlying collection allows to perform a doPut request on it.
   *
   * @return True if the underlying collection allows to perform a doPut request on it.
   */
  public boolean isSupportingPutOperation () {
    return _controller instanceof PutCollectionOperation;
  }

  /**
   * Perform a put request on the underlying collection.
   *
   * @param request The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> put (@NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class)
             .map(JsonUtils::mapToNullNodeIfNull)
             .flatMap(this::doPut)
             .flatMap(result -> result.toCollectionServerResponse(request));
  }

  /**
   * Perform a put request on the underlying collection.
   *
   * @param json The json content to put.
   *
   * @return The result of the doPut operation.
   */
  private @NonNull Mono<@NonNull PutResult> doPut (@NonNull final JsonNode json) {
    if (isSupportingPutOperation()) {
      @NonNull final PutCollectionOperation operation = (PutCollectionOperation) _controller;

      try {
        return Mono.just(operation.put(json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.PUT, getSupportedMethods()));
    }
  }

  /**
   * Return true if the underlying collection allows to perform a doPatch request on it.
   *
   * @return True if the underlying collection allows to perform a doPatch request on it.
   */
  public boolean isSupportingPatchOperation () {
    return _controller instanceof PatchCollectionOperation;
  }

  /**
   * Perform a patch request on the underlying collection.
   *
   * @param request The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> patch (@NonNull final ServerRequest request) {
    return request.bodyToMono(JsonNode.class).map(JsonUtils::mapToNullNodeIfNull).flatMap(this::doPatch);
  }

  /**
   * Perform a patch request on the underlying collection.
   *
   * @param json The json content to patch.
   *
   * @return The server response to send.
   */
  private @NonNull Mono<ServerResponse> doPatch (@NonNull final JsonNode json) {
    if (isSupportingPatchOperation()) {
      @NonNull final PatchCollectionOperation operation = (PatchCollectionOperation) _controller;

      try {
        operation.patch(json);
        return ServerResponse.ok().build();
      } catch (@NonNull final Throwable exception) {
        return Mono.error(exception);
      }
    } else {
      return Mono.error(new MethodNotAllowedException(HttpMethod.PATCH, getSupportedMethods()));
    }
  }

  /**
   * Return true if the underlying collection allows to perform a doPatch request on it.
   *
   * @return True if the underlying collection allows to perform a doPatch request on it.
   */
  public boolean isSupportingDeleteOperation () {
    return _controller instanceof DeleteCollectionOperation;
  }

  /**
   * Perform a delete request on the underlying collection.
   *
   * @param request The received request.
   *
   * @return The response to send.
   */
  public @NonNull Mono<ServerResponse> delete (@NonNull final ServerRequest request) {
    return Mono.just(request).map(ServerRequest::queryParams).map(APIRequest::new).flatMap(this::doDelete)
               .flatMap(x -> ServerResponse.ok().build());
  }

  /**
   * Perform a delete request on the underlying collection.
   *
   * @param request The api request to apply.
   *
   * @return The number of deleted elements.
   */
  public @NonNull Mono<@NonNull Long> doDelete (@NonNull final APIRequest request) {
    if (isSupportingDeleteOperation()) {
      @NonNull final DeleteCollectionOperation operation = (DeleteCollectionOperation) _controller;

      try {
        return Mono.just(operation.delete(request));
      } catch (@NonNull final InvalidAPIRequestException exception) {
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
    if (isSupportingPostOperation()) result.add(HttpMethod.POST);
    if (isSupportingPutOperation()) result.add(HttpMethod.PUT);
    if (isSupportingPatchOperation()) result.add(HttpMethod.PATCH);
    if (isSupportingDeleteOperation()) result.add(HttpMethod.DELETE);

    return result;
  }

  /**
   * Return the underlying collection controller.
   *
   * @return The underlying collection controller.
   */
  public @NonNull CollectionController<Model> getController () {
    return _controller;
  }
}
