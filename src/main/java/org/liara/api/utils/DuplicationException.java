package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class DuplicationException
  extends Exception
{
  public DuplicationException (@NonNull final String message, @NonNull final Throwable cause) {
    super(message, cause);
  }
}
