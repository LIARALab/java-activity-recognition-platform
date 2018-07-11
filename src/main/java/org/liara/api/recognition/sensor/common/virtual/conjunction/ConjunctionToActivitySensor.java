package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.List;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.entity.state.ActivityStateCreationSchema;
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
    
    final List<Conjunction> conjunctions = _data.getConjunctions(
      getConfiguration().getInputs(),
      getConfiguration().getNodes()
    );
    
    _schemaManager.flush();
    _schemaManager.clear();
    
    for (int index = 0; index < conjunctions.size(); ++index) {
      final Conjunction conjunction = conjunctions.get(index);
      emit(conjunction);
      
      if (index % 500 == 0 && index != 0) {
        _schemaManager.flush();
        _schemaManager.clear();
      }
    }
    
    _schemaManager.flush();
    _schemaManager.clear();
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

  private void emit (
    @NonNull final Conjunction conjunction
  ) {
    final ActivityStateCreationSchema creation = new ActivityStateCreationSchema();
    
    creation.setEmittionDate(conjunction.getEmittionDate());
    creation.setStart(conjunction.getStart());
    creation.setEnd(conjunction.getEnd());
    creation.setTag(getConfiguration().getTag());
    creation.setSensor(getSensor());
    
    for (final ActivationState state : conjunction.getStates()) {
      creation.correlate("activationOf" + state.getNodeIdentifier(), state);
    }
    
    _schemaManager.execute(creation);
  }
}
