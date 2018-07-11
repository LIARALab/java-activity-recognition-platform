package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.collection.SensorCollection;
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
  private final Set<Long> _inputs = new HashSet<>();
  
  @NonNull
  private final Set<Long> _nodes = new HashSet<>();
  
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
  
  @ValidApplicationEntityReference(collection = SensorCollection.class)
  public Set<Long> getInputs () {
    return Collections.unmodifiableSet(_inputs);
  }
  
  public void setInputs (@Nullable final Collection<Long> inputs) {
    _inputs.clear();
    if (inputs != null) _inputs.addAll(inputs);
  }
  
  @ValidApplicationEntityReference(collection = SensorCollection.class)
  public Set<Long> getNodes () {
    return Collections.unmodifiableSet(_nodes);
  }
  
  public void setNodes (@Nullable final Collection<Long> inputs) {
    _nodes.clear();
    if (inputs != null) _nodes.addAll(inputs);
  }
  
  public ConjunctionToActivitySensorConfiguration clone () {
    return new ConjunctionToActivitySensorConfiguration(this);
  }
}
