package org.liara.api.event.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.state.State;
import org.liara.api.utils.Duplicator;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class UpdateStateEvent
  extends ApplicationEvent
{
  /**
   *
   */
  private static final long serialVersionUID = 8116556813252780522L;

  @NonNull
  private final State _oldValue;

  @NonNull
  private final State _newValue;

  public UpdateStateEvent (
    @NonNull final Object source, @NonNull final State oldValue, @NonNull final State newValue
  ) {
    super(source);
    _oldValue = Duplicator.duplicate(oldValue);
    _newValue = Duplicator.duplicate(newValue);
  }

  public UpdateStateEvent (
    @NonNull final UpdateStateEvent toCopy
  ) {
    super(toCopy);
    _oldValue = toCopy.getOldValue();
    _newValue = toCopy.getNewValue();
  }

  public @NonNull State getNewValue () {
    return Duplicator.duplicate(_newValue);
  }

  public @NonNull State getOldValue () {
    return Duplicator.duplicate(_oldValue);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_newValue, _oldValue);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (getClass().isInstance(other)) {
      @NonNull final UpdateStateEvent otherEvent = (UpdateStateEvent) other;

      return Objects.equals(getSource(), otherEvent.getSource()) &&
             Objects.equals(getTimestamp(), otherEvent.getTimestamp()) &&
             Objects.equals(_newValue, otherEvent.getNewValue()) &&
             Objects.equals(_oldValue, otherEvent.getOldValue());
    }

    return false;
  }
}
