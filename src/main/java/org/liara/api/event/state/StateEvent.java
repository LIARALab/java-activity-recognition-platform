package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.state.State;
import org.liara.api.utils.Duplicator;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class StateEvent
  extends ApplicationEvent
{
  public StateEvent (
    @NonNull final Object source, @NonNull final State state
  )
  {
    super(source);
    _state = Duplicator.duplicate(state);
  }

  public StateEvent (
    @NonNull final StateEvent toCopy
  )
  {
    super(toCopy);
    _state = toCopy.getState();
  }

  public @NonNull State getState () {
    return Duplicator.duplicate(_state);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_state);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (getClass().isInstance(other)) {
      @NonNull final StateEvent otherEvent = (StateEvent) other;

      return Objects.equals(getSource(), otherEvent.getSource()) &&
             Objects.equals(getTimestamp(), otherEvent.getTimestamp()) && Objects.equals(_state, otherEvent.getState());
    }

    return false;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 784473504749676317L;
  
  @NonNull
  private final State _state;

}
