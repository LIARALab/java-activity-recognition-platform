package org.liara.api.collection.route;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.route.APIRouteElement;

public class BaseAPIRouteElement implements APIRouteElement
{
  @NonNull
  private final String _token;

  protected BaseAPIRouteElement (@NonNull final String token) {
    _token = token;
  }

  @Override
  public @NonNull String getToken () {
    return _token;
  }
}
