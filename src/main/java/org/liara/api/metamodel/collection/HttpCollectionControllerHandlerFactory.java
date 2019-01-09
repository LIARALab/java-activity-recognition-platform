package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.WeakHashMap;

public final class HttpCollectionControllerHandlerFactory
{
  @NonNull
  private static final WeakHashMap<@NonNull CollectionController<?>, @NonNull HttpCollectionControllerHandler<?>> INSTANCES = new WeakHashMap<>();

  @SuppressWarnings("unchecked")
  public static <Model> @NonNull HttpCollectionControllerHandler<Model> getHandlerFor (
    @NonNull final CollectionController<Model> controller
  )
  {
    @NonNull final HttpCollectionControllerHandler<Model> result;

    if (HttpCollectionControllerHandlerFactory.INSTANCES.containsKey(controller)) {
      result =
        (HttpCollectionControllerHandler<Model>) HttpCollectionControllerHandlerFactory.INSTANCES.get(controller);
    } else {
      result = new HttpCollectionControllerHandler<>(controller);

      HttpCollectionControllerHandlerFactory.INSTANCES.put(controller, result);
    }

    return result;
  }
}
