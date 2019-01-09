package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.utils.ObjectIdentifiers;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * An object that allows to register and request collection controllers.
 */
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionControllerManager
{
  @NonNull
  private final Map<@NonNull String, @NonNull CollectionController<?>> _controllers;

  /**
   * Create a new empty manager.
   */
  public CollectionControllerManager () {
    _controllers = new HashMap<>();
  }

  /**
   * Create a copy of another manager.
   *
   * @param toCopy A collection controller manager to copy.
   */
  public CollectionControllerManager (@NonNull final CollectionControllerManager toCopy) {
    _controllers = new HashMap<>(toCopy.getControllers());
  }

  public void put (@NonNull final String name, @NonNull final CollectionController<?> controller) {
    if (_controllers.containsKey(name)) {
      if (!Objects.equals(_controllers.get(name), controller)) {
        throw new Error(
          "Unable to register the controller " + ObjectIdentifiers.getIdentifier(controller) + " with name \"" + name +
          "\" because another controller was already registered with the same name (" +
          ObjectIdentifiers.getIdentifier(_controllers.get(name)) +
          ") please use unique collection names for your controllers."
        );
      }
    } else {
      _controllers.put(name, controller);
    }
  }

  public void remove (@NonNull final String name) {
    if (_controllers.containsKey(name)) {
      _controllers.remove(name);
    }
  }

  public boolean contains (@NonNull final String name) {
    return _controllers.containsKey(name);
  }

  public @NonNull CollectionController<?> get (@NonNull final String name) {
    return _controllers.get(name);
  }

  public @NonNull Map<@NonNull String, @NonNull CollectionController<?>> getControllers () {
    return Collections.unmodifiableMap(_controllers);
  }

  public @NonNull Set<@NonNull String> getControllerNames () {
    return Collections.unmodifiableSet(_controllers.keySet());
  }

  public int size () {
    return _controllers.size();
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
