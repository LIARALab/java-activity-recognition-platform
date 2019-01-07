package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionControllerManager
  implements Loggable
{
  @NonNull
  private final Map<@NonNull String, @NonNull Object> _controllersByName;

  @NonNull
  private final Set<@NonNull Object> _controllers;

  public CollectionControllerManager () {
    _controllersByName = new HashMap<>();
    _controllers = new HashSet<>();
  }

  public CollectionControllerManager (@NonNull final CollectionControllerManager toCopy) {
    _controllersByName = new HashMap<>(toCopy.getControllersByName());
    _controllers = new HashSet<>(toCopy.getControllers());
  }

  public void register (@NonNull final Object controller) {
    @NonNull final String name = CollectionControllers.getName(controller);

    if (_controllersByName.containsKey(name)) {
      if (!Objects.equals(_controllersByName.get(name), controller)) {
        throw new Error(
          "Unable to register the controller " + controller.getClass().toString() + " with name \"" + name +
          "\" because another controller was already registered with the same name (" +
          _controllersByName.get(name).getClass().toString() + ") please use unique collection names " +
          "for your controllers.");
      }
    } else {
      info("collectionController.registered " + name + " " + CollectionControllers.getManagedType(controller) + " " +
           controller.toString());
      _controllersByName.put(name, controller);
      _controllers.add(controller);
    }
  }

  public void unregister (@NonNull final Object controller) {
    @NonNull final String name = CollectionControllers.getName(controller);

    if (_controllers.contains(controller)) {
      info("collectionController.unregistered " + name + " " + CollectionControllers.getManagedType(controller) + " " +
           controller.toString());
      _controllersByName.remove(name);
      _controllers.remove(controller);
    }
  }

  public boolean contains (@NonNull final String name) {
    return _controllersByName.containsKey(name);
  }

  public boolean contains (@NonNull final Object controller) {
    return _controllers.contains(controller);
  }

  public @NonNull Object get (@NonNull final String name) {
    return _controllersByName.get(name);
  }

  public @NonNull Set<@NonNull Object> getControllers () {
    return Collections.unmodifiableSet(_controllers);
  }

  public @NonNull Map<@NonNull String, @NonNull Object> getControllersByName () {
    return Collections.unmodifiableMap(_controllersByName);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_controllers);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof CollectionControllerManager) {
      @NonNull final CollectionControllerManager otherManager = (CollectionControllerManager) other;

      return Objects.equals(_controllers, otherManager.getControllers());
    }

    return false;
  }
}
