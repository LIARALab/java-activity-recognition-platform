package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ConjunctionToActivitySensorConfiguration
       implements SensorConfiguration
{
  @Nullable
  private String _tag;
  
  @NonNull
  private final Set<ApplicationEntityReference<Sensor>> _inputs = new HashSet<>();
  
  @NonNull
  private final Set<ApplicationEntityReference<Node>> _nodes = new HashSet<>();
  
  public ConjunctionToActivitySensorConfiguration () {
    _tag = null;
  }
  
  public ConjunctionToActivitySensorConfiguration (
    @NonNull final ConjunctionToActivitySensorConfiguration toCopy
  ) {
    _tag = toCopy.getTag();
    _inputs.addAll(toCopy.getInputs());
    _nodes.addAll(toCopy.getNodes());
  }
  
  @Required
  public String getTag () {
    return _tag;
  }
  
  public void setTag (@Nullable final String tag) {
    _tag = tag;
  }
  
  @ValidApplicationEntityReference
  @Required
  public Set<ApplicationEntityReference<Sensor>> getInputs () {
    return Collections.unmodifiableSet(_inputs);
  }
  
  public void setInputs (@Nullable final Collection<Long> inputs) {
    _inputs.clear();
    if (inputs != null) _inputs.addAll(ApplicationEntityReference.of(Sensor.class, inputs));
  }
  
  @ValidApplicationEntityReference
  @Required
  public Set<ApplicationEntityReference<Node>> getNodes () {
    return Collections.unmodifiableSet(_nodes);
  }
  
  public void setNodes (@Nullable final Collection<Long> inputs) {
    _nodes.clear();
    if (inputs != null) _nodes.addAll(ApplicationEntityReference.of(Node.class, inputs));
  }
  
  public ConjunctionToActivitySensorConfiguration clone () {
    return new ConjunctionToActivitySensorConfiguration(this);
  }
}
