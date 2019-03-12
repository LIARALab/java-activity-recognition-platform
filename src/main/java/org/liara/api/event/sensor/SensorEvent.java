package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.utils.Duplicator;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class SensorEvent
  extends ApplicationEvent
{
  public SensorEvent (
    @NonNull final Object source,
    @NonNull final Sensor sensor
  ) {
    super(source);
    _sensor = Duplicator.duplicate(sensor);
  }

  public SensorEvent (@NonNull final SensorEvent toCopy) {
    super(toCopy);
    _sensor = toCopy.getSensor();
  }

  /**
   *
   */
  private static final long serialVersionUID = 784473504749676317L;

  @NonNull
  private final Sensor _sensor;

  public @NonNull Sensor getSensor () {
    return Duplicator.duplicate(_sensor);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_sensor);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (getClass().isInstance(other)) {
      @NonNull final SensorEvent otherEvent = (SensorEvent) other;

      return Objects.equals(getSource(), otherEvent.getSource()) &&
             Objects.equals(getTimestamp(), otherEvent.getTimestamp()) &&
             Objects.equals(_sensor, otherEvent.getSensor());
    }

    return false;
  }

}
