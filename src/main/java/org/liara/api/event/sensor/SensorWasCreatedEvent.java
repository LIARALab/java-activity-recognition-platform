package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

public class SensorWasCreatedEvent
  extends SensorEvent
{
  public SensorWasCreatedEvent (@NonNull final Object source, @NonNull final Sensor sensor) {
    super(source, sensor);
  }

  public SensorWasCreatedEvent (@NonNull final SensorWasCreatedEvent toCopy) {
    super(toCopy);
  }
}
