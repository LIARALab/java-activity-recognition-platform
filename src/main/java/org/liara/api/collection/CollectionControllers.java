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
   * Assert that the given object is a valid collection controller instance.
   * <p>
   * If the given object is not a valid collection controller instance, an error will be thrown with the given message
   * followed by a description of it's cause.
   *
   * @param object An object to validate.
   * @param error  An error message to throw if the given object is not a valid collection controller instance.
   */
  static void assertThatIsACollectionController (@NonNull final Object object, @NonNull final String error) {
    if (!CollectionControllers.isCollectionController(object)) {
      throw new Error(error + " because the given object " + object.toString() + " of type " +
                      AopUtils.getTargetClass(object).toString() + " is not annotated as a " +
                      CollectionController.class.toString() +
                      " and consequently, is not a collection controller instance.");
    }
  }

  /**
   * Return the controller annotation if present, or throw an error.
   *
   * @param object An object to check.
   *
   * @return The given object's controller annotation if present.
   */
  static @NonNull CollectionController getControllerAnnotation (@NonNull final Object object) {
    assertThatIsACollectionController(object, "Unable to get the collection controller configuration");

    @NonNull final Class<?> objectClass = AopUtils.getTargetClass(object);

    return objectClass.getAnnotation(CollectionController.class);
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
