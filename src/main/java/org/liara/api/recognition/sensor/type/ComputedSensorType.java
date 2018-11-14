package org.liara.api.recognition.sensor.type;

public interface ComputedSensorType
  extends SensorType
{
  @Override
  default boolean isNative () {
    return false;
  }
}
