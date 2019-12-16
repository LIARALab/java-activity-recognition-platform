package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

public class WillDeleteSensorEvent
  extends SensorEvent
{
  public WillDeleteSensorEvent (@NonNull final Object source, @NonNull final Sensor sensor) {
    super(source, sensor);
  }

  public WillDeleteSensorEvent (@NonNull final WillDeleteSensorEvent toCopy) {
    super(toCopy);
  }
}
