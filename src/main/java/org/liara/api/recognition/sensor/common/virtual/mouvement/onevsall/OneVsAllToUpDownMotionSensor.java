package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.BooleanStateRepository;
import org.liara.api.data.repository.SensorRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@UseSensorConfigurationOfType(OneVsAllToUpDownMotionSensorConfiguration.class)
@EmitStateOfType(BooleanState.class)
@Component
@Scope("prototype")
public class OneVsAllToUpDownMotionSensor 
       extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;
  
  @NonNull
  private final BooleanStateRepository _flags;
  
  @NonNull
  private final SensorRepository _sensors;
  
  @Autowired
  public OneVsAllToUpDownMotionSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final BooleanStateRepository flags,
    @NonNull final SensorRepository sensors
  ) {
    _schemaManager = schemaManager;
    _flags = flags;
    _sensors = sensors;
  }
  
  public OneVsAllToUpDownMotionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      OneVsAllToUpDownMotionSensorConfiguration.class
    );
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  public List<ApplicationEntityReference<Sensor>> getInputSensors () {
    return _sensors.getSensorsOfTypeIntoNode(
      NativeMotionSensor.class, 
      getSensor().getNode().getRoot().getReference().as(Node.class)
    ).stream()
     .filter(sensor -> getConfiguration().isIgnoredInput(sensor) == false)
     .map(Sensor::getReference)
     .collect(Collectors.toList());
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);
    
    final List<BooleanState> states = _flags.findAllWithValue(getInputSensors(), true);
    
    final OneVsAllToUpDownMotionSensorConfiguration configuration = getConfiguration();
    
    _schemaManager.flush();
    _schemaManager.clear();
    
    BooleanState previous = null;
    
    for (int index = 0; index < states.size(); ++index) {
      final BooleanState current = states.get(index);
      
      if (previous == null || areOfSameType(previous, current) == false) {
        emit(current, configuration.isValidInput(current));
      }
      
      previous = current;
    
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
    
    if (event.getState().getModel().getSensor().isOfType(NativeMotionSensor.class) &&
        ((BooleanState) event.getState().getModel()).getValue() &&
      getConfiguration().isIgnoredInput(event.getState().getModel()) == false
    ) {
      onMotionStateWasCreated((BooleanState) event.getState().getModel());
    }
  }

  public void onMotionStateWasCreated (
    @NonNull final BooleanState created
  ) {
    if (created.getValue() == false) return;
    if (isDuplicate(created)) return;
    
    final List<ApplicationEntityReference<Sensor>> inputSensors = getInputSensors();
    final Optional<BooleanState> previous = _flags.findPreviousWithValue(created, inputSensors, true);
    final Optional<BooleanState> next = _flags.findNextWithValue(created, inputSensors, true);
    
    if (previous.isPresent() == false) {
      onLeftMotionStateWasCreated(created, next);
    } else if (!areOfSameType(previous.get(), created)) {
      onRightMotionStateWasCreated(created, next);
    }
  }

  private boolean isDuplicate (@NonNull final BooleanState created) {
    final List<BooleanState>     states = _flags.findAllAt(created, getInputSensors());
    final Optional<BooleanState> state  = states.stream().filter(x -> x.getValue()).findFirst();

    return !state.get().equals(created);
  }

  private void onLeftMotionStateWasCreated (
    @NonNull final BooleanState created, 
    @NonNull final Optional<BooleanState> next
  ) {
    if (next.isPresent() && areOfSameType(created, next.get())) {
      move(next.get(), created);
    } else {
      emit(created);
    }
  }

  private void onRightMotionStateWasCreated (
    @NonNull final BooleanState created, 
    @NonNull final Optional<BooleanState> next
  ) {
    if (next.isPresent() == false) {
      emit(created);
    } else if (areOfSameType(created, next.get())) {
      move(next.get(), created);
    } else {
      emit(created);
      emit(next.get());
    }
  }

  private boolean areOfSameType (
    @NonNull final BooleanState left, 
    @NonNull final BooleanState right
  ) {
    return getConfiguration().isValidInput(
      left
    ) == getConfiguration().isValidInput(right);
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateWasMutatedEvent event
  ) {
    super.stateWasMutated(event);
    
    if (
      event.getNewValue().getModel().getSensor().isOfType(NativeMotionSensor.class) &&
      getConfiguration().isIgnoredInput(event.getNewValue().getModel()) == false
    ) {
      onMotionStateWasMutated((BooleanStateSnapshot) event.getOldValue(), (BooleanState) event.getNewValue().getModel()
      );
    }
  }

  public void onMotionStateWasMutated (
    @NonNull final BooleanStateSnapshot base, 
    @NonNull final BooleanState updated
  ) {
    final Optional<BooleanState> correlation = _flags.findFirstWithCorrelation(
      "base", updated.getReference(), getSensor().getReference()
    );
    
    if (correlation.isPresent()) {
      onCorrelledMotionStateWasMutated(base, updated);
    } else if (updated.getValue()) {
      onMotionStateWasCreated(updated);
    }
  }

  private void onCorrelledMotionStateWasMutated (
    @NonNull final BooleanStateSnapshot base, 
    @NonNull final BooleanState updated
  ) {
    final List<ApplicationEntityReference<Sensor>> inputSensors = getInputSensors();
    final Optional<BooleanState> previous = _flags.findPreviousWithValue(base.getEmittionDate(), inputSensors, true);
    final Optional<BooleanState> next = _flags.findNextWithValue(base.getEmittionDate(), inputSensors, true);
    
    if (previous.isPresent() && Objects.equals(previous.get(), updated)) {
      move(updated, updated);
    } else {
      onRightCorreledMotionStateWasMutated(updated, next);
    }
  }
  
  private void onRightCorreledMotionStateWasMutated (
    @NonNull final BooleanState updated, 
    @NonNull final Optional<BooleanState> next
  ) {
    if (next.isPresent() == false) {
      delete(updated);
      onMotionStateWasCreated(updated);
    } else if (Objects.equals(next.get(), updated)) {
      move(updated, updated);
    } else if (areOfSameType(next.get(), updated)) {
      move(updated, next.get());
      onMotionStateWasCreated(updated);
    } else {
      delete(updated);
      delete(next.get());
      onMotionStateWasCreated(updated);
    }
  }
  
  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);
    
    if (event.getState().getState().is(BooleanState.class)) {
      final BooleanState state = _flags.find(event.getState().getState().as(BooleanState.class)).get();
      
      if (
        state.getSensor().isOfType(NativeMotionSensor.class) && 
        state.getValue()
      ) {
        onMotionStateWillBeDeleted(state);
      }
    }    
  }
  
  public void onMotionStateWillBeDeleted (@NonNull final BooleanState state) {
    if (_flags.findFirstWithCorrelation(
      "base", state.getReference(), getSensor().getReference()
    ).isPresent() == false) return;
    
    final List<ApplicationEntityReference<Sensor>> inputs = getInputSensors();
    final Optional<BooleanState> previous = _flags.findPreviousWithValue(
      state.getEmittionDate(), inputs, true
    );
    
    final Optional<BooleanState> next = _flags.findNextWithValue(
      state.getEmittionDate(), inputs, true
    );
    
    if (previous.isPresent() == false) {
      onLeftMotionStateWillBeDeleted(state, next);
    } else {
      onRightMotionStateWillBeDeleted(state, next);
    }
  }

  private void onRightMotionStateWillBeDeleted (
    @NonNull final BooleanState state, 
    @NonNull final Optional<BooleanState> next
  ) {
    if (next.isPresent() == false) {
      delete(state);
    } else if (areOfSameType(state, next.get())) {
      move(state, next.get());
    } else {
      delete(state);
      delete(next.get());
    }
  }

  private void onLeftMotionStateWillBeDeleted (
    @NonNull final BooleanState state, 
    @NonNull final Optional<BooleanState> next
  ) {
    if (next.isPresent() == false) {
      delete(state);
    } else if (areOfSameType(state, next.get())) {
      move(state, next.get());
    } else {
      delete(state);
    }
  }

  private void delete (@NonNull final BooleanState state) {
    _schemaManager.execute(new StateDeletionSchema(
      _flags.findFirstWithCorrelation(
        "base", state.getReference(), getSensor().getReference()
      ).get()
    ));
  }

  private void move (
    @NonNull final BooleanState from, 
    @NonNull final BooleanState to
  ) {
    final State toMove = _flags.findFirstWithCorrelation(
      "base", from.getReference(), getSensor().getReference()
    ).get();

    final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema();
    mutation.setState(toMove);
    mutation.setEmittionDate(to.getEmittionDate());
    if (Objects.equals(from, to) == false) {
      mutation.correlate("base", to);
    }
    _schemaManager.execute(mutation);
  }

  private void emit (@NonNull final BooleanState created) {
    emit(created, getConfiguration().isValidInput(created));
  }
  
  private void emit (@NonNull final State state, final boolean up) {
    final BooleanStateCreationSchema creation = new BooleanStateCreationSchema();
    creation.setEmittionDate(state.getEmittionDate());
    creation.setSensor(getSensor());
    creation.setValue(up);
    creation.correlate("base", state);
    
    _schemaManager.execute(creation);
  }
}
