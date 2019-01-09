package org.liara.api.collection.route;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

public class APIRouteParserToken
{
  @NonNull
  private final String _token;

  @NonNegative
  private final int _position;

  @NonNull
  private final String _fullPath;

  public APIRouteParserToken (
    @NonNull final String token,
    @NonNegative final int position,
    @NonNull final String fullPath
  ) {
    _token = token;
    _position = position;
    _fullPath = fullPath;
  }

  public @NonNull String getToken () {
    return _token;
  }

  public @NonNegative int getPosition () {
    return _position;
  }

  public @NonNull String getFullPath () {
    return _fullPath;
  }
}
