package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class UpDownToActivationSensorConfiguration
       implements SensorConfiguration
{
  @Nullable
  private Long _inputSensor;
  
  @Nullable
  private Long _activatedNode;
  
  public UpDownToActivationSensorConfiguration () {
    _inputSensor = null;
    _activatedNode = null;
  }
  
  public UpDownToActivationSensorConfiguration (
    @NonNull final UpDownToActivationSensorConfiguration toCopy
  ) {
    _inputSensor = toCopy.getInputSensor();
    _activatedNode = toCopy.getActivatedNode();
  }
  
  @IdentifierOfEntityInCollection(collection = SensorCollection.class)
  @Required
  public Long getInputSensor () {
    return _inputSensor;
  }
  
  @JsonSetter
  public void setInputSensor (@Nullable final Long sensor) {
    _inputSensor = sensor;
  }
  
  public void setInputSensor (@Nullable final Sensor sensor) {
    _inputSensor = (sensor == null) ? null : sensor.getIdentifier();
  }
  
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  public Long getActivatedNode () {
    return _activatedNode;
  }
  
  @JsonSetter
  public void setActivatedNode (@Nullable final Long node) {
    _activatedNode = node;
  }
  
  public void setActivatedNode (@Nullable final Node node) {
    _activatedNode = (node == null) ? null : node.getIdentifier();
  }
  
  public UpDownToActivationSensorConfiguration clone () {
    return new UpDownToActivationSensorConfiguration(this);
  }
}
