package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.aop.support.AopUtils;

public interface CollectionControllers
{
  /**
   * Return true if the given object is a collection controller.
   *
   * @param object An object to check.
   *
   * @return True if the given object is a collection controller.
   */
  static boolean isCollectionController (@NonNull final Object object) {
    return AopUtils.getTargetClass(object).isAnnotationPresent(CollectionController.class);
  }

  /**
   * Return the controller annotation if present, or throw an error.
   *
   * @param object An object to check.
   *
   * @return The given object's controller annotation if present.
   */
  static @NonNull CollectionController getControllerAnnotation (@NonNull final Object object) {
    @NonNull final Class<?> objectClass = AopUtils.getTargetClass(object);

    if (objectClass.isAnnotationPresent(CollectionController.class)) {
      return objectClass.getAnnotation(CollectionController.class);
    } else {
      throw new Error("Unable to get the collection controller configuration of the object " + objectClass.toString() +
                      " because the given object is not annotated as a " + CollectionController.class);
    }
  }

  /**
   * Return the name of the collection managed by the given controller.
   *
   * @param object A collection controller.
   *
   * @return The name of the collection controller managed collection.
   */
  static @NonNull String getName (@NonNull final Object object) {
    return getControllerAnnotation(object).name();
  }

  /**
   * Return the type of entities stored into the collection managed by the given controller.
   *
   * @param object A collection controller.
   *
   * @return The type of entities stored into the collection managed by the given controller.
   */
  static @NonNull Class<?> getManagedType (@NonNull final Object object) {
    return getControllerAnnotation(object).managedType();
  }
}
