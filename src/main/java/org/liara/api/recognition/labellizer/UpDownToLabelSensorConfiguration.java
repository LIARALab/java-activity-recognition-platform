package org.liara.api.recognition.labellizer;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.api.validation.Required;

public class UpDownToLabelSensorConfiguration
       implements SensorConfiguration
{
  @Nullable
  private       Long                                         _inputSensor;

  @Nullable
  private       String                                       _label;

  public UpDownToLabelSensorConfiguration () {
    _inputSensor = null;
    _label = null;
  }

  public UpDownToLabelSensorConfiguration (
    @Nullable final Sensor inputSensor, @Nullable final String label
  ) {
    _inputSensor = inputSensor.getIdentifier();
    _label = label;
  }

  public UpDownToLabelSensorConfiguration (
    @NonNull final UpDownToLabelSensorConfiguration toCopy
  ) {
    _inputSensor = toCopy.getInputSensor();
    _label = toCopy.getLabel();
  }

  @Required
  public @NonNull String getLabel () {
    return _label;
  }

  public void setLabel (@NonNull final String label) {
    _label = label;
  }

  @ApplicationEntityReference(Sensor.class)
  @Required
  public @NonNull Long getInputSensor () {
    return _inputSensor;
  }

  @JsonSetter
  public void setInputSensor (
    @Nullable final Long sensor
  )
  {
    _inputSensor = sensor;
  }

  public @NonNull UpDownToLabelSensorConfiguration clone () {
    return new UpDownToLabelSensorConfiguration(this);
  }
}
