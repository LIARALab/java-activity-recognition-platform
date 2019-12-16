package org.liara.api.event.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

public class DidCreateSensorEvent
  extends SensorEvent
{
  public DidCreateSensorEvent (@NonNull final Object source, @NonNull final Sensor sensor) {
    super(source, sensor);
  }

  public DidCreateSensorEvent (@NonNull final DidCreateSensorEvent toCopy) {
    super(toCopy);
  }
}
