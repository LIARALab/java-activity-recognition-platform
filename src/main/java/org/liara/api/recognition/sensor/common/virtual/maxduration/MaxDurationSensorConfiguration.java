package org.liara.api.recognition.sensor.common.virtual.maxduration;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

public class MaxDurationSensorConfiguration
  implements SensorConfiguration
{
  @Nullable
  private ApplicationEntityReference<Sensor> _inputSensor;

  @Nullable
  private Duration _maxDuration;

  @Nullable
  private String _tag;

  public MaxDurationSensorConfiguration () {
    _inputSensor = null;
    _maxDuration = null;
    _tag = "FAILURE";
  }

  public MaxDurationSensorConfiguration (@NonNull final MaxDurationSensorConfiguration toCopy) {
    _inputSensor = toCopy.getInputSensor();
    _maxDuration = toCopy.getMaxDuration();
    _tag = toCopy.getTag();
  }

  public Duration getMaxDuration () {
    return _maxDuration;
  }

  public void setMaxDuration (@Nullable final Duration maxDuration) {
    _maxDuration = maxDuration;
  }

  public ApplicationEntityReference<Sensor> getInputSensor () {
    return _inputSensor;
  }

  public void setInputSensor (@Nullable final ApplicationEntityReference<Sensor> inputSensor) {
    _inputSensor = inputSensor;
  }

  public String getTag () {
    return _tag;
  }

  public void setTag (@Nullable final String tag) {
    _tag = tag;
  }

  @Override
  public MaxDurationSensorConfiguration clone () {
    return new MaxDurationSensorConfiguration(this);
  }
}
