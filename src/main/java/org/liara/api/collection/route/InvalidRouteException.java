package org.liara.api.collection.route;

import org.checkerframework.checker.nullness.qual.NonNull;

public class InvalidRouteException extends Exception
{
  public InvalidRouteException () {
  }

  public InvalidRouteException (@NonNull final String message) {
    super(message);
  }

  public InvalidRouteException (@NonNull final String message, @NonNull final Throwable cause) {
    super(message, cause);
  }

  public InvalidRouteException (@NonNull final Throwable cause) {
    super(cause);
  }
}
