package org.liara.api.recognition.sensor.common.virtual.condition;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ConditionSensorConfiguration
  implements SensorConfiguration
{
  @Nullable
  private Long _inputSensor;

  public ConditionSensorConfiguration () {
    _inputSensor = null;
  }

  public ConditionSensorConfiguration (
    @NonNull final ConditionSensorConfiguration toCopy
  ) {
    _inputSensor = toCopy.getInputSensor();
  }

  @Required
  @ApplicationEntityReference(Sensor.class)
  public @Nullable
  Long getInputSensor () {
    return _inputSensor;
  }

  @JsonSetter
  public void setInputSensor (@Nullable final Long sensor) {
    _inputSensor = sensor;
  }

  public void setInputSensor (@Nullable final Sensor sensor) {
    _inputSensor = (sensor == null) ? null : sensor.getIdentifier();
  }
}
