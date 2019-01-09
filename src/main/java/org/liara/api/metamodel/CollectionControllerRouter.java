package org.liara.api.metamodel;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class CollectionControllerRouter implements RouterFunction<ServerResponse>
{

  @Override
  public Mono<HandlerFunction<ServerResponse>> route (
    @NonNull final ServerRequest request
  ) {
    return null;
  }
}
