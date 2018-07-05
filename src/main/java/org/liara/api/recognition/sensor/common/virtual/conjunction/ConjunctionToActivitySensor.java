package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.List;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@UseSensorConfigurationOfType(ConjunctionToActivitySensorConfiguration.class)
@EmitStateOfType(ActivityState.class)
@Component
@Scope("prototype")
public class ConjunctionToActivitySensor 
       extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;
  
  @NonNull
  private final ConjunctionToActivitySensorData _data;
  
  @Autowired
  public ConjunctionToActivitySensor(
    @NonNull final SchemaManager schemaManager,
    @NonNull final ConjunctionToActivitySensorData data
  ) {
    _schemaManager = schemaManager;
    _data = data;
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  public ConjunctionToActivitySensorConfiguration getConfiguration () {
    return getSensor().getConfiguration().as(
      ConjunctionToActivitySensorConfiguration.class
    );
  }
  
  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);
    
    final List<Conjunction> _conjunctions = _data.getConjunctions(getConfiguration().getInputs());
    
  }

  @Override
  public void stateWasCreated (
    @NonNull final StateWasCreatedEvent event
  ) {
    super.stateWasCreated(event);
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateWasMutatedEvent event
  ) {
    super.stateWasMutated(event);
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);
  }
}
