package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionControllerHandlerFactory
{
  @NonNull
  private final WeakHashMap<@NonNull CollectionController<?>, @NonNull CollectionControllerHandler<?>> _handlers;

  public CollectionControllerHandlerFactory () {
    _handlers = new WeakHashMap<>();
  }

  @SuppressWarnings("unchecked") // Asserted by the _handlers map structure.
  public <Model> @NonNull CollectionControllerHandler<Model> getCollectionControllerHandler (
    @NonNull final CollectionController<Model> collectionController
  ) {
    @NonNull final CollectionControllerHandler<Model> result;

    if (!_handlers.containsKey(collectionController)) {
      result = new CollectionControllerHandler<>(collectionController);
      _handlers.put(collectionController, result);
    }

    return (CollectionControllerHandler<Model>) _handlers.get(collectionController);
  }
}
