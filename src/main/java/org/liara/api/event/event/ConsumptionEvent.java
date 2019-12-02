package org.liara.api.event.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

@JsonIgnoreProperties({"source", "unnamedModule", "classLoader"})
public abstract class ConsumptionEvent
  extends ApplicationEvent
{
  @NonNull
  private final ApplicationEvent _event;

  public ConsumptionEvent (@NonNull final Object source, @NonNull final ApplicationEvent event) {
    super(source);
    _event = event;
  }

  public @NonNull ApplicationEvent getEvent () {
    return _event;
  }

  public @NonNull String getType () {
    return getClass().getSimpleName();
  }

  @Override
  public int hashCode () {
    return Objects.hash(getEvent());
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (getClass().isInstance(other)) {
      @NonNull final ConsumptionEvent otherEvent = (ConsumptionEvent) other;

      return Objects.equals(getSource(), otherEvent.getSource()) &&
             Objects.equals(getTimestamp(), otherEvent.getTimestamp()) &&
             Objects.equals(getEvent(), otherEvent.getEvent());
    }

    return false;
  }
}
