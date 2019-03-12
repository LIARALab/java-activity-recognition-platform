package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;

public class WillDeleteStateEvent
  extends StateEvent
{
  public WillDeleteStateEvent (@NonNull final Object source, @NonNull final State state) {
    super(source, state);
  }

  public WillDeleteStateEvent (@NonNull final WillCreateStateEvent toCopy) {
    super(toCopy);
  }
}
