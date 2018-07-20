package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class UpDownToActivationSensorConfiguration
       implements SensorConfiguration
{
  @NonNull
  private ApplicationEntityReference<Sensor> _inputSensor;
  
  @NonNull
  private ApplicationEntityReference<Node> _activatedNode;
  
  public UpDownToActivationSensorConfiguration () {
    _inputSensor = ApplicationEntityReference.empty(Sensor.class);
    _activatedNode = ApplicationEntityReference.empty(Node.class);
  }
  
  public UpDownToActivationSensorConfiguration (
    @Nullable final Sensor inputSensor,
    @Nullable final Node activatedNode
  ) {
    _inputSensor = ApplicationEntityReference.empty(Sensor.class);
    _activatedNode = ApplicationEntityReference.empty(Node.class);
    setInputSensor(inputSensor);
    setActivatedNode(activatedNode);
  }
  
  public UpDownToActivationSensorConfiguration (
    @NonNull final UpDownToActivationSensorConfiguration toCopy
  ) {
    _inputSensor = toCopy.getInputSensor();
    _activatedNode = toCopy.getActivatedNode();
  }
  
  @ValidApplicationEntityReference
  @Required
  public ApplicationEntityReference<Sensor> getInputSensor () {
    return _inputSensor;
  }
  
  @JsonSetter
  public void setInputSensor (@Nullable final Long sensor) {
    _inputSensor = ApplicationEntityReference.of(Sensor.class, sensor);
  }
  
  public void setInputSensor (@Nullable final Sensor sensor) {
    _inputSensor = (sensor == null) ? ApplicationEntityReference.empty(Sensor.class) 
                                    : ApplicationEntityReference.of(sensor);
  }
  
  @ValidApplicationEntityReference
  public ApplicationEntityReference<Node> getActivatedNode () {
    return _activatedNode;
  }
  
  @JsonSetter
  public void setActivatedNode (@Nullable final Long node) {
    _activatedNode = ApplicationEntityReference.of(Node.class, node);
  }
  
  public void setActivatedNode (@Nullable final Node node) {
    _activatedNode = (node == null) ? ApplicationEntityReference.empty(Node.class)
                                    : ApplicationEntityReference.of(node);
  }
  
  public UpDownToActivationSensorConfiguration clone () {
    return new UpDownToActivationSensorConfiguration(this);
  }
}
