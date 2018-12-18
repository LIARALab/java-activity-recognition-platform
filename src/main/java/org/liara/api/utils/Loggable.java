package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Logger;

public interface Loggable
{
  default @NonNull Logger getLogger () {
    return Logger.getLogger(getClass().getName());
  }

  default void info (@NonNull final String message) {
    getLogger().info(message);
  }

  default void warning (@NonNull final String message) {
    getLogger().warning(message);
  }

  default void severe (@NonNull final String message) {
    getLogger().severe(message);
  }
}
