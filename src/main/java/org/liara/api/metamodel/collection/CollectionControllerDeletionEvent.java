package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationEvent;

public class CollectionControllerDeletionEvent
  extends ApplicationEvent
{
  @NonNull
  private final String _name;

  public CollectionControllerDeletionEvent (
    @NonNull final Object source,
    @NonNull final String name
  ) {
    super(source);
    _name = name;
  }

  public CollectionControllerDeletionEvent (
    @NonNull final CollectionControllerDeletionEvent toCopy
  ) {
    super(toCopy.getSource());
    _name = toCopy.getName();
  }

  public @NonNull String getName () {
    return _name;
  }
}
