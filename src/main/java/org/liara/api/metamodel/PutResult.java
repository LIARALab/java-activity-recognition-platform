package org.liara.api.metamodel;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class PutResult
{
  private final boolean _replaced;

  @NonNull
  private final Long _identifier;

  private PutResult (
    final boolean replaced, @NonNull final Long identifier
  )
  {
    _replaced = replaced;
    _identifier = identifier;
  }

  public PutResult (@NonNull final PutResult toCopy) {
    _replaced = toCopy.wasReplaced();
    _identifier = toCopy.getIdentifier();
  }

  public @NonNull Mono<ServerResponse> toCollectionServerResponse (@NonNull final ServerRequest request) {
    return wasCreated() ? ServerResponse.created(request.uri().resolve("./" + getIdentifier())).build()
                        : ServerResponse.ok().build();
  }

  public @NonNull Mono<ServerResponse> toModelServerResponse (@NonNull final ServerRequest request) {
    return wasCreated() ? ServerResponse.created(request.uri()).build() : ServerResponse.ok().build();
  }

  public boolean wasReplaced () {
    return _replaced;
  }

  public boolean wasCreated () {
    return !_replaced;
  }

  public @NonNull Long getIdentifier () {
    return _identifier;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_identifier, _replaced);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof PutResult) {
      @NonNull final PutResult otherResult = (PutResult) other;

      return Objects.equals(_identifier, otherResult.getIdentifier()) && Objects.equals(
        _replaced,
        otherResult.wasReplaced()
      );
    }

    return false;
  }
}
