package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class Duplicator
{
  public static <Source> @NonNull Source duplicate (@NonNull final Source toDuplicate) {
    try {
      return tryToDuplicate(toDuplicate);
    } catch (@NonNull final DuplicationException exception) {
      throw new Error(exception);
    }
  }

  public static <Source> @NonNull Source tryToDuplicate (
    @NonNull final Source toDuplicate
  )
  throws DuplicationException
  {
    @NonNull final Class<? extends Source>       type        =
      (Class<? extends Source>) AopUtils.getTargetClass(
      toDuplicate);
    @NonNull final Constructor<? extends Source> constructor = getCopyConstructorOrFail(type);

    try {
      return constructor.newInstance(toDuplicate);
    } catch (@NonNull final IllegalAccessException exception) {
      throw new DuplicationException(
        "Unable to duplicate the given instance of " + type.toString() + " because the given instance " +
        " copy constructor " + type.toString() + "#" + type.getSimpleName() + "(" + "@NonNull final " +
        type.getSimpleName() + ") is of " + getVisibility(constructor) + " visibility.", exception);
    } catch (@NonNull final InstantiationException | InvocationTargetException exception) {
      throw new DuplicationException(
        "Unable to duplicate the given instance of " + type.toString() + " because an exception was throwed during " +
        "the instantiation of a copy instance : " + exception.getMessage(), exception);
    }
  }

  private static @NonNull String getVisibility (@NonNull final Constructor constructor) {
    final int modifier = constructor.getModifiers();

    if (Modifier.isPublic(modifier)) {
      return "public";
    } else if (Modifier.isPrivate(modifier)) {
      return "private";
    } else if (Modifier.isProtected(modifier)) {
      return "protected";
    } else {
      return "package";
    }
  }

  private static <Source> @NonNull Constructor<? extends Source> getCopyConstructorOrFail (
    @NonNull final Class<? extends Source> type
  )
  throws DuplicationException
  {
    try {
      return type.getConstructor(type);
    } catch (@NonNull final NoSuchMethodException exception) {
      throw new DuplicationException(
        "Unable to duplicate the given instance of " + type.toString() + " because the given instance " +
        " does not declare a valid copy constructor " + type.toString() + "#" + type.getSimpleName() + "(" +
        "@NonNull final " + type.getSimpleName() + ")", exception);
    }
  }
}
