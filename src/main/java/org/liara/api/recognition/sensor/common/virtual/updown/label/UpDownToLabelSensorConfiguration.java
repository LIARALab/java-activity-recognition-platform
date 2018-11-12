package org.liara.api.recognition.sensor.common.virtual.updown.label;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;

import java.util.ArrayList;
import java.util.List;

public class UpDownToLabelSensorConfiguration
       implements SensorConfiguration
{
  @NonNull
  private final List<@NonNull String>                        _tags;
  @NonNull
  private       ApplicationEntityReference<? extends Sensor> _inputSensor;
  @Nullable
  private       String                                       _label;

  public UpDownToLabelSensorConfiguration () {
    _inputSensor = ApplicationEntityReference.empty(Sensor.class);
    _tags = new ArrayList<>();
    _label = null;
  }

  public UpDownToLabelSensorConfiguration (
    @Nullable final Sensor inputSensor, @Nullable final String label
  ) {
    _inputSensor = inputSensor == null ? ApplicationEntityReference.empty(Sensor.class) : inputSensor.getReference();
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

  @ValidApplicationEntityReference
  @Required
  public @NonNull ApplicationEntityReference<? extends Sensor> getInputSensor () {
    return _inputSensor;
  }

  @JsonSetter
  public void setInputSensor (
    @Nullable final ApplicationEntityReference<Sensor> sensor
  )
  {
    _inputSensor = sensor == null ? ApplicationEntityReference.empty(Sensor.class) : sensor;
  }

  public @NonNull UpDownToLabelSensorConfiguration clone () {
    return new UpDownToLabelSensorConfiguration(this);
  }
}
