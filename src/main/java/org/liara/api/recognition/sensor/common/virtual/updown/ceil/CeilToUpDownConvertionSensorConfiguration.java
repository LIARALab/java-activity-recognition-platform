package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CeilToUpDownConvertionSensorConfiguration implements SensorConfiguration
{
  @NonNull
  private InterpolationType _interpolation;

  @Nullable
  private Long _inputSensor;
  
  private double _ceil;
  
  public CeilToUpDownConvertionSensorConfiguration () {
    _interpolation = InterpolationType.NONE;
    _inputSensor = null;
    _ceil = 0;
  }
  
  public CeilToUpDownConvertionSensorConfiguration (
    @NonNull final CeilToUpDownConvertionSensorConfiguration toCopy
  ) {
    _interpolation = toCopy.getInterpolationType();
    _inputSensor = toCopy.getInputSensor();
    _ceil = toCopy.getCeil();
  }
  
  public double getCeil () {
    return _ceil;
  }
  
  public void setCeil (final double ceil) {
    _ceil = ceil;
  }
  
  @Required
  @ApplicationEntityReference(Sensor.class)
  public Long getInputSensor () {
    return _inputSensor;
  }

  public void setInputSensor (@Nullable final Sensor sensor) {
    _inputSensor = sensor.getIdentifier();
  }
  
  @JsonSetter
  public void setInputSensor (@Nullable final Long sensor) {
    _inputSensor = sensor;
  }
  
  @Required
  public InterpolationType getInterpolationType () {
    return _interpolation;
  }
  
  public void setInterpolationType (@Nullable final InterpolationType type) {
    if (type == null) {
      _interpolation = InterpolationType.NONE;
    } else {
      _interpolation = type;
    }
  }
  
  @Override
  public CeilToUpDownConvertionSensorConfiguration clone () {
    return new CeilToUpDownConvertionSensorConfiguration(this);
  }
}
