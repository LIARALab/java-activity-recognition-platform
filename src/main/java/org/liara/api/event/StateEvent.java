package org.liara.api.event;

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

  public static class WillBeCreated
    extends StateEvent
  {
    public WillBeCreated (@NonNull final Object source, @NonNull final State state) {
      super(source, state);
    }

    public WillBeCreated (@NonNull final WillBeCreated toCopy) {
      super(toCopy);
    }
  }

  public static class WasCreated
    extends StateEvent
  {
    public WasCreated (@NonNull final Object source, @NonNull final State state) {
      super(source, state);
    }

    public WasCreated (@NonNull final WasCreated toCopy) {
      super(toCopy);
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 784473504749676317L;
  
  @NonNull
  private final State _state;

  public static class WillBeDeleted
    extends StateEvent
  {
    public WillBeDeleted (@NonNull final Object source, @NonNull final State state) {
      super(source, state);
    }

    public WillBeDeleted (@NonNull final WillBeCreated toCopy) {
      super(toCopy);
    }
  }

  public static class WasDeleted
    extends StateEvent
  {
    public WasDeleted (@NonNull final Object source, @NonNull final State state) {
      super(source, state);
    }

    public WasDeleted (@NonNull final WasCreated toCopy) {
      super(toCopy);
    }
  }

  public static class MutationEvent
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

    public MutationEvent (
      @NonNull final Object source, @NonNull final State oldValue, @NonNull final State newValue
    )
    {
      super(source);
      _oldValue = Duplicator.duplicate(oldValue);
      _newValue = Duplicator.duplicate(newValue);
    }

    public MutationEvent (
      @NonNull final MutationEvent toCopy
    )
    {
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
        @NonNull final MutationEvent otherEvent = (MutationEvent) other;

        return Objects.equals(getSource(), otherEvent.getSource()) &&
               Objects.equals(getTimestamp(), otherEvent.getTimestamp()) &&
               Objects.equals(_newValue, otherEvent.getNewValue()) &&
               Objects.equals(_oldValue, otherEvent.getOldValue());
      }

      return false;
    }
  }

  public static class WillBeMutated
    extends MutationEvent
  {
    public WillBeMutated (@NonNull final Object source, @NonNull final State oldState, @NonNull final State newState) {
      super(source, oldState, newState);
    }

    public WillBeMutated (@NonNull final WillBeMutated toCopy) {
      super(toCopy);
    }
  }

  public static class WasMutated
    extends MutationEvent
  {
    public WasMutated (@NonNull final Object source, @NonNull final State oldState, @NonNull final State newState) {
      super(source, oldState, newState);
    }

    public WasMutated (@NonNull final WasMutated toCopy) {
      super(toCopy);
    }
  }
}
