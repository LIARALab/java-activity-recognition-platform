package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;

public class WillUpdateStateEvent
  extends UpdateStateEvent
{
  public WillUpdateStateEvent (
    @NonNull final Object source,
    @NonNull final State oldState,
    @NonNull final State newState
  ) {
    super(source, oldState, newState);
  }

  public WillUpdateStateEvent (@NonNull final WillUpdateStateEvent toCopy) {
    super(toCopy);
  }
}
