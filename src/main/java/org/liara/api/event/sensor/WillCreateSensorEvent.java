package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

public class WillCreateSensorEvent
  extends SensorEvent
{
  public WillCreateSensorEvent (@NonNull final Object source, @NonNull final Sensor sensor) {
    super(source, sensor);
  }

  public WillCreateSensorEvent (@NonNull final WillCreateSensorEvent toCopy) {
    super(toCopy);
  }
}
