package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import java.util.List;
import java.util.Objects;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanStateCreationSchema;
import org.liara.api.data.entity.state.BooleanStateMutationSchema;
import org.liara.api.data.entity.state.BooleanStateSnapshot;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateDeletionSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@UseSensorConfigurationOfType(OneVsAllToUpDownMotionSensorConfiguration.class)
@EmitStateOfType(BooleanState.class)
@Component
@Scope("prototype")
/**
 * OPTIMIZABLE : dont emit repetitions of falses / true
 *
 */
public class OneVsAllToUpDownMotionSensor 
       extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;
  
  @NonNull
  private final OneVsAllToUpDownMotionSensorData _data;
  
  @NonNull
  private final StateDeletionSchema _deletion = new StateDeletionSchema();

  @NonNull
  private final BooleanStateMutationSchema _mutation = new BooleanStateMutationSchema();
  
  @NonNull
  private final BooleanStateCreationSchema _creation = new BooleanStateCreationSchema();
  
  @Autowired
  public OneVsAllToUpDownMotionSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final OneVsAllToUpDownMotionSensorData data
  ) {
    _schemaManager = schemaManager;
    _data = data;
  }
  
  public OneVsAllToUpDownMotionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      OneVsAllToUpDownMotionSensorConfiguration.class
    );
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);
    
    final List<BooleanState> states = _data.fetchAll(getSensor());
    final OneVsAllToUpDownMotionSensorConfiguration configuration = getConfiguration();
    
    _schemaManager.flush();
    _schemaManager.clear();
    
    for (int index = 0; index < states.size(); ++index) {  
      final BooleanState state = states.get(index);
  
      if (configuration.isValidInput(state)) {
        emit(state, true);
      } else if (configuration.isInvalidInput(state)) {
        emit(state, false);
      }
    
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
    
    if (
      NativeMotionSensor.class.isAssignableFrom(
        event.getState().getModel().getSensor().getTypeClass()
      ) && BooleanState.class.cast(event.getState().getModel()).getValue()
    ) {
      onMotionStateWasCreated(event.getState().getModel());
    }
  }

  private void onMotionStateWasCreated (
    @NonNull final State model
  ) {
    if (getConfiguration().isValidInput(model)) {
      emit(model, true);
    } else if (getConfiguration().isIgnoredInput(model)) {
      emit(model, false);
    }
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateWasMutatedEvent event
  ) {
    super.stateWasMutated(event);
    
    if (
      NativeMotionSensor.class.isAssignableFrom(
        event.getState().getModel().getSensor().getTypeClass()
      )
    ) {
      onMotionStateWasMutated(
        BooleanStateSnapshot.class.cast(event.getState()),
        BooleanState.class.cast(event.getState().getModel())
      );
    }
  }

  private void onMotionStateWasMutated (
    @NonNull final BooleanStateSnapshot previous, 
    @NonNull final BooleanState next
  ) {
    if (getConfiguration().isIgnoredInput(next)) return;
    
    if (previous.getValue() != next.getValue()) {
      if (next.getValue()) onMotionStateWasCreated(next);
      else delete(next);
    }
    
    if (!Objects.equals(previous.getEmittionDate(), next.getEmittionDate())) {
      reemit(next);
    }
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);
    
    final State state = _data.fetch(event.getState().getState());
    
    if (
      NativeMotionSensor.class.isAssignableFrom(
        state.getSensor().getTypeClass()
      ) && BooleanState.class.cast(state).getValue()
    ) {
      delete(state);
    }
  }
  
  private void delete (@NonNull final State state) {
    _deletion.clear();
    _deletion.setState(_data.fetchCorrelated(state, getSensor()));
    
    _schemaManager.execute(_deletion);
  }

  private void reemit (@NonNull final BooleanState next) {
    if (getConfiguration().isValidInput(next)) {
      reemit(next, true);
    } else {
      reemit(next, false);
    }
  }

  private void reemit (@NonNull final State state, final boolean up) {
    final BooleanState correlated = _data.fetchCorrelated(state, getSensor());
    
    _mutation.clear();
    _mutation.setState(correlated);
    _mutation.setEmittionDate(state.getEmittionDate());
    _mutation.setValue(up);
    _mutation.correlate("base", state);
    
    _schemaManager.execute(_mutation);
  }

  private void emit (@NonNull final State state, final boolean up) {
    _creation.clear();
    _creation.setEmittionDate(state.getEmittionDate());
    _creation.setSensor(getSensor());
    _creation.setValue(up);
    _creation.correlate("base", state);
    
    _schemaManager.execute(_creation);
  }
}
