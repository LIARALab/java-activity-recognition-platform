package org.liara.api.recognition.sensor.common.virtual.condition;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ConditionSensorConfiguration
  implements SensorConfiguration
{
  @NonNull
  private ApplicationEntityReference<Sensor> _inputSensor;

  public ConditionSensorConfiguration () {
    _inputSensor = ApplicationEntityReference.empty(Sensor.class);
  }

  public ConditionSensorConfiguration (
    @NonNull final ConditionSensorConfiguration toCopy
  )
  {
    _inputSensor = toCopy.getInputSensor();
  }

  @Required
  @ValidApplicationEntityReference
  public ApplicationEntityReference<Sensor> getInputSensor () {
    return _inputSensor;
  }

  public void setInputSensor (@Nullable final Sensor sensor) {
    _inputSensor = (sensor == null) ? ApplicationEntityReference.empty(Sensor.class) : ApplicationEntityReference.of(
      sensor);
  }

  @JsonSetter
  public void setInputSensor (@Nullable final Long sensor) {
    _inputSensor = ApplicationEntityReference.of(Sensor.class, sensor);
  }

  @Override
  public ConditionSensorConfiguration clone () {
    return new ConditionSensorConfiguration(this);
  }
}
