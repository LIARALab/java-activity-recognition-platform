package org.liara.api.collection.configuration;

import org.checkerframework.checker.index.qual.LessThan;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RequestPath
  implements Iterable<@NonNull String>
{
  @NonNull
  private final List<@NonNull String> _parameters;

  public RequestPath () {
    _parameters = new ArrayList<>();
  }

  public RequestPath (@NonNull final String... path) {
    _parameters = new ArrayList<>(Arrays.asList(path));
  }

  public RequestPath (@NonNull final List<@NonNull String> path) {
    _parameters = new ArrayList<>(path);
  }

  public RequestPath (@NonNull final RequestPath toCopy) {
    _parameters = new ArrayList<>(toCopy.getParameters());
  }

  public @NonNull RequestPath next () {
    return new RequestPath(_parameters.subList(1, _parameters.size()));
  }

  public @NonNull RequestPath concat (@NonNull final String... path) {
    return new RequestPath(Stream.concat(_parameters.stream(), Arrays.stream(path)).collect(Collectors.toList()));
  }

  public @NonNull RequestPath concat (@NonNull final List<@NonNull String> path) {
    return new RequestPath(Stream.concat(_parameters.stream(), path.stream()).collect(Collectors.toList()));
  }

  public @NonNull RequestPath concat (@NonNull final RequestPath path) {
    return new RequestPath(Stream.concat(_parameters.stream(), path.getParameters().stream())
                             .collect(Collectors.toList()));
  }

  public @NonNull String last () {
    return _parameters.get(_parameters.size() - 1);
  }

  public @NonNull String first () {
    return _parameters.get(0);
  }

  public int size () {
    return _parameters.size();
  }

  public @NonNull String getParameter (@NonNegative @LessThan("this.size()") final int index) {
    return _parameters.get(index);
  }

  public @NonNull List<@NonNull String> getParameters () {
    return Collections.unmodifiableList(_parameters);
  }

  @Override
  public @NonNull Iterator<@NonNull String> iterator () {
    return _parameters.iterator();
  }

  @Override
  public int hashCode () {
    return Objects.hash(_parameters);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof RequestPath) {
      @NonNull final RequestPath otherPath = (RequestPath) other;

      return Objects.equals(_parameters, otherPath.getParameters());
    }

    return false;
  }

  @Override
  public @NonNull String toString () {
    return String.join(".", _parameters);
  }
}
