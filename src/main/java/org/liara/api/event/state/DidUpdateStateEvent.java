package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;

public class DidUpdateStateEvent
  extends UpdateStateEvent
{
  public DidUpdateStateEvent (
    @NonNull final Object source,
    @NonNull final State oldState,
    @NonNull final State newState
  ) {
    super(source, oldState, newState);
  }

  public DidUpdateStateEvent (@NonNull final DidUpdateStateEvent toCopy) {
    super(toCopy);
  }
}
