package org.liara.api.recognition.sensor.type;

import org.liara.api.data.entity.SensorType;

public interface ComputedSensorType
  extends SensorType
{
  @Override
  default boolean isNative () {
    return false;
  }
}
