package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

public class SensorWillBeCreatedEvent
  extends SensorEvent
{
  public SensorWillBeCreatedEvent (@NonNull final Object source, @NonNull final Sensor sensor) {
    super(source, sensor);
  }

  public SensorWillBeCreatedEvent (@NonNull final SensorWillBeCreatedEvent toCopy) {
    super(toCopy);
  }
}
