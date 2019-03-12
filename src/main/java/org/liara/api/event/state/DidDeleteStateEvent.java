package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;

public class DidDeleteStateEvent
  extends StateEvent
{
  public DidDeleteStateEvent (@NonNull final Object source, @NonNull final State state) {
    super(source, state);
  }

  public DidDeleteStateEvent (@NonNull final DidCreateStateEvent toCopy) {
    super(toCopy);
  }
}
