package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class ObjectIdentifiers
{
  public static @NonNull String getIdentifier (@NonNull final Object object) {
    return object.getClass().toString() + "@" + System.identityHashCode(object);
  }
}
