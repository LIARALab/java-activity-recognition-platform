package org.liara.api.metamodel;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.collection.CollectionController;
import org.liara.api.metamodel.collection.CollectionControllerHandler;
import org.liara.api.metamodel.collection.CollectionControllerHandlerFactory;
import org.liara.api.metamodel.collection.CollectionControllerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class APIRouter
  implements RouterFunction<ServerResponse>
{
  @NonNull
  private final CollectionControllerManager _collectionControllerManager;

  @NonNull
  private final CollectionControllerHandlerFactory _collectionControllerHandlerFactory;

  @Autowired
  public APIRouter (
    @NonNull final CollectionControllerManager collectionControllerManager,
    @NonNull final CollectionControllerHandlerFactory collectionControllerHandlerFactory
  ) {
    _collectionControllerManager = collectionControllerManager;
    _collectionControllerHandlerFactory = collectionControllerHandlerFactory;
  }

  @Override
  public @NonNull Mono<@NonNull HandlerFunction<ServerResponse>> route (
    @NonNull final ServerRequest request
  ) {
    @NonNull final ServerRequestPathNavigator navigator = new ServerRequestPathNavigator(request);

    if (navigator.hasNext()) {
      @NonNull final String collectionName = navigator.next().toLowerCase();

      if (_collectionControllerManager.contains(collectionName)) {
        @NonNull final CollectionController<?> controller = _collectionControllerManager.get(collectionName);
        @NonNull final CollectionControllerHandler<?> handler =
          _collectionControllerHandlerFactory.getCollectionControllerHandler(controller);

        @NonNull Mono<@NonNull HandlerFunction<ServerResponse>>
      }
    }

    return Mono.empty();
  }
}
