package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class UpDownToLabelSensorConfiguration
       implements SensorConfiguration
{
  @NonNull
  private ApplicationEntityReference<Sensor> _inputSensor;

  @Nullable
  private String _label;

  public UpDownToLabelSensorConfiguration () {
    _inputSensor = ApplicationEntityReference.empty(Sensor.class);
    _label = null;
  }

  public UpDownToLabelSensorConfiguration (
    @NonNull final ApplicationEntityReference<Sensor> inputSensor, @NonNull final String label
  ) {
    _inputSensor = inputSensor;
    _label = label;
  }

  public UpDownToLabelSensorConfiguration (
    @NonNull final UpDownToLabelSensorConfiguration toCopy
  ) {
    _inputSensor = toCopy.getInputSensor();
    _label = toCopy.getLabel();
  }
  
  @ValidApplicationEntityReference
  @Required
  public ApplicationEntityReference<Sensor> getInputSensor () {
    return _inputSensor;
  }
  
  @JsonSetter
  public void setInputSensor (@Nullable final ApplicationEntityReference<Sensor> sensor) {
    _inputSensor = sensor;
  }
  
  public void setInputSensor (@Nullable final Sensor sensor) {
    _inputSensor = (sensor == null) ? ApplicationEntityReference.empty(Sensor.class) 
                                    : ApplicationEntityReference.of(sensor);
  }

  @Required
  public @Nullable
  String getLabel () {
    return _label;
  }

  public void setLabel (@Nullable final String label) {
    _label = label;
  }

  public UpDownToLabelSensorConfiguration clone () {
    return new UpDownToLabelSensorConfiguration(this);
  }
}
