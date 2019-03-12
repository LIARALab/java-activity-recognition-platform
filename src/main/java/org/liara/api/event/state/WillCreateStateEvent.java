package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;

public class WillCreateStateEvent
  extends StateEvent
{
  public WillCreateStateEvent (@NonNull final Object source, @NonNull final State state) {
    super(source, state);
  }

  public WillCreateStateEvent (@NonNull final WillCreateStateEvent toCopy) {
    super(toCopy);
  }
}
