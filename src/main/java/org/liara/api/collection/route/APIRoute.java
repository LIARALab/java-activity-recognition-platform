package org.liara.api.collection.route;

import org.checkerframework.checker.index.qual.LessThan;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class APIRoute
  implements Iterable<@NonNull APIRouteElement>
{
  @NonNull
  private final List<@NonNull APIRouteElement> _elements;

  /**
   * Create a new empty collection route.
   */
  public APIRoute () {
    _elements = new ArrayList<>();
  }

  public APIRoute (@NonNull final APIRouteElement element) {
    _elements = new ArrayList<>();
    _elements.add(element);
  }

  public APIRoute (@NonNull final Iterable<@NonNull APIRouteElement> elements) {
    _elements = new ArrayList<>();
    elements.forEach(_elements::add);
  }

  public APIRoute (@NonNull final Iterable<@NonNull APIRouteElement> ...parts) {
    _elements = new ArrayList<>();

    for (@NonNull final Iterable<@NonNull APIRouteElement> elements : parts) {
      elements.forEach(_elements::add);
    }
  }

  public APIRoute (@NonNull final APIRoute toCopy) {
    _elements = new ArrayList<>();
    toCopy.forEach(_elements::add);
  }

  public @NonNull APIRouteElement getLast () {
    return _elements.get(_elements.size() - 1);
  }

  public @NonNull APIRouteElement getFirst () {
    return _elements.get(0);
  }

  public @NonNull APIRouteElement get (@NonNegative @LessThan("this.size()") final int index) {
    return _elements.get(index);
  }

  public @NonNull APIRoute concat (@NonNull final APIRoute route) {
    return new APIRoute(this, route);
  }

  public @NonNull APIRoute concat (@NonNull final APIRouteElement element) {
    return new APIRoute(this, Collections.singleton(element));
  }

  public @NonNull APIRoute subRoute (@NonNegative @LessThan("this.size()") final int start) {
    return new APIRoute(this._elements.subList(start, this._elements.size()));
  }

  public @NonNull APIRoute subRoute (
    @NonNegative @LessThan("this.size()") int fromIndex,
    @NonNegative @LessThan("this.size()") int toIndex
  ) {
    return new APIRoute(this._elements.subList(fromIndex, toIndex));
  }

  public int size () {
    return _elements.size();
  }

  @Override
  public Iterator<@NonNull APIRouteElement> iterator () {
    return Collections.unmodifiableList(_elements).iterator();
  }

  public @NonNull List<@NonNull APIRouteElement> getElements () {
    return Collections.unmodifiableList(_elements);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_elements);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof APIRoute) {
      @NonNull final APIRoute otherRoute = (APIRoute) other;

      return Objects.equals(_elements, otherRoute.getElements());
    }

    return false;
  }
}
