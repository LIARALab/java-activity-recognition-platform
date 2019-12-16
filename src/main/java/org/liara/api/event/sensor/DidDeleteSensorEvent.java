package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

public class DidDeleteSensorEvent
  extends SensorEvent
{
  public DidDeleteSensorEvent (@NonNull final Object source, @NonNull final Sensor sensor) {
    super(source, sensor);
  }

  public DidDeleteSensorEvent (@NonNull final DidDeleteSensorEvent toCopy) {
    super(toCopy);
  }
}
