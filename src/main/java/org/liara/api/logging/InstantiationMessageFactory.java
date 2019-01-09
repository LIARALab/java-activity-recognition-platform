package org.liara.api.logging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.utils.ObjectIdentifiers;

public final class InstantiationMessageFactory
{
  public static @NonNull String instantiating (@NonNull final Class<?> type) {
    return "instantiating " + type.toString();
  }

  public static @NonNull String instantiating (@NonNull final String alias) {
    return "instantiating " + alias;
  }

  public static @NonNull String instantiated (@NonNull final Object object) {
    return "instantiated " + object.getClass().toString() + " as " + getIdentifier(object);
  }

  public static @NonNull String instantiated (@NonNull final Class<?> type, @NonNull final Object object) {
    return "instantiated " + type.toString() + " as " + getIdentifier(object);
  }

  public static @NonNull String instantiated (@NonNull final String alias, @NonNull final Object object) {
    return "instantiated " + alias + " as " + getIdentifier(object);
  }

  private static @NonNull String getIdentifier (@NonNull final Object object) {
    return ObjectIdentifiers.getIdentifier(object);
  }
}
