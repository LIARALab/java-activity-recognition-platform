package org.liara.api.metamodel.model;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.WeakHashMap;

public final class HttpModelControllerHandlerFactory
{
  @NonNull
  private static final WeakHashMap<@NonNull ModelController<?>, @NonNull HttpModelControllerHandler<?>> INSTANCES =
    new WeakHashMap<>();

  @SuppressWarnings("unchecked")
  public static <Model> @NonNull HttpModelControllerHandler<Model> getHandlerFor (
    @NonNull final ModelController<Model> controller
  )
  {
    @NonNull final HttpModelControllerHandler<Model> result;

    if (HttpModelControllerHandlerFactory.INSTANCES.containsKey(controller)) {
      result = (HttpModelControllerHandler<Model>) HttpModelControllerHandlerFactory.INSTANCES.get(controller);
    } else {
      result = new HttpModelControllerHandler<>(controller);

      HttpModelControllerHandlerFactory.INSTANCES.put(controller, result);
    }

    return result;
  }
}
