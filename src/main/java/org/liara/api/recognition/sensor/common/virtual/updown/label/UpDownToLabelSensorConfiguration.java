package org.liara.api.recognition.sensor.common.virtual.updown.label;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.api.validation.Required;

import java.util.ArrayList;
import java.util.List;

public class UpDownToLabelSensorConfiguration
       implements SensorConfiguration
{
  @NonNull
  private final List<@NonNull String>                        _tags;

  @Nullable
  private       Long                                         _inputSensor;

  @Nullable
  private       String                                       _label;

  public UpDownToLabelSensorConfiguration () {
    _inputSensor = null;
    _tags = new ArrayList<>();
    _label = null;
  }

  public UpDownToLabelSensorConfiguration (
    @Nullable final Sensor inputSensor, @Nullable final String label
  ) {
    _inputSensor = inputSensor.getIdentifier();
    _tags = new ArrayList<>();
    _label = label;
  }

  public UpDownToLabelSensorConfiguration (
    @NonNull final UpDownToLabelSensorConfiguration toCopy
  ) {
    _inputSensor = toCopy.getInputSensor();
    _tags = new ArrayList<>(toCopy.getTags());
    _label = toCopy.getLabel();
  }

  public @NonNull List<@NonNull String> getTags () {
    return _tags;
  }

  public void setTags (@Nullable final List<@NonNull String> tags) {
    _tags.clear();

    if (tags != null) _tags.addAll(tags);
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
