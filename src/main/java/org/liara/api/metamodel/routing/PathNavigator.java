package org.liara.api.metamodel.routing;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.http.server.PathContainer;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class PathNavigator
  implements Iterator<@NonNull String>
{
  @NonNull
  private final PathContainer _path;

  @NonNegative
  private int _cursor;

  public PathNavigator (@NonNull final ServerRequest request) {
    _path = request.pathContainer();
    _cursor = 0;
  }

  public PathNavigator (@NonNull final PathContainer path) {
    _path = path;
    _cursor = 0;
  }

  public PathNavigator (@NonNull final String path) {
    _path = PathContainer.parsePath(path);
    _cursor = 0;
  }

  public PathNavigator (@NonNull final PathNavigator toCopy) {
    _path = toCopy.getPath();
    _cursor = toCopy.getCursor();
  }

  public boolean isAtStart () {
    return _cursor == 0;
  }

  public boolean isAtEnd () {
    return _cursor == _path.elements().size();
  }

  @Override
  public boolean hasNext () {
    for (int index = _cursor; index < _path.elements().size(); ++index) {
      if (_path.elements().get(index) instanceof PathContainer.PathSegment) return true;
    }

    return false;
  }

  @Override
  public @NonNull String next () {
    for (int index = _cursor; index < _path.elements().size(); ++index) {
      if (_path.elements().get(index) instanceof PathContainer.PathSegment) {
        _cursor = index + 1;
        return _path.elements().get(index).value();
      }
    }

    throw new NoSuchElementException();
  }

  public @NonNull PathContainer getPath () {
    return _path;
  }

  public @NonNegative int getCursor () {
    return _cursor;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_path, _cursor);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof PathNavigator) {
      @NonNull final PathNavigator otherNavigator = (PathNavigator) other;

      return Objects.equals(_path, otherNavigator.getPath()) &&
             Objects.equals(_cursor, otherNavigator.getCursor());
    }

    return false;
  }
}
