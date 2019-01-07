package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionControllerRoutingFactory
{
  @NonNull
  private final CollectionControllerManager _collectionControllerManager;

  @Autowired
  public CollectionControllerRoutingFactory (@NonNull final CollectionControllerManager collectionControllerManager) {
    _collectionControllerManager = collectionControllerManager;
  }

  public @NonNull RouterFunction<ServerResponse> get () {
    @Nullable RouterFunction<ServerResponse> result = null;

    for (@NonNull final Object controller : _collectionControllerManager.getControllers()) {
      if (result == null) {
        result = get(controller);
      } else {
        result = result.and(get(controller));
      }
    }

    if (result == null) {
      throw new Error("No registered collection controller.");
    } else {
      return result;
    }
  }

  public @NonNull RouterFunction<ServerResponse> get (@NonNull final Object controller) {
    @NonNull final String                      name    = CollectionControllers.getName(controller);
    @NonNull final CollectionControllerHandler handler = CollectionControllerHandler.instantiate(controller);

    return RouterFunctions.nest(RequestPredicates.path("/" + name), handler.createRouterFunction());
  }
}
