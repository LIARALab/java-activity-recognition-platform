package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.BooleanStateRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.data.schema.BooleanStateCreationSchema;
import org.liara.api.data.schema.BooleanStateMutationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.data.schema.StateDeletionSchema;
import org.liara.api.event.StateEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

  @NonNull
  private final NodeRepository _nodes;
  
  @Autowired
  public OneVsAllToUpDownMotionSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final BooleanStateRepository flags,
    @NonNull final SensorRepository sensors,
    @NonNull final NodeRepository nodes
  ) {
    _schemaManager = schemaManager;
    _flags = flags;
    _sensors = sensors;
    _nodes = nodes;
  }
  
  public OneVsAllToUpDownMotionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      OneVsAllToUpDownMotionSensorConfiguration.class
    );
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }

  public List<ApplicationEntityReference<? extends Sensor>> getInputSensors () {
    final List<ApplicationEntityReference<? extends Sensor>> result = new ArrayList<>();

    _sensors.getSensorsOfTypeIntoNode(NativeMotionSensor.class, _nodes.getRoot(getSensor().getNode()).getReference()
    ).stream()
            .filter((Sensor sensor) -> !getConfiguration().isIgnoredInput(sensor))
            .forEach((Sensor sensor) -> result.add(sensor.getReference()));

    return result;
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
    @NonNull final StateEvent event
  ) {
    super.stateWasCreated(event);
    
    if (event.getState().getSensor().isOfType(NativeMotionSensor.class) &&
        ((BooleanState) event.getState()).getValue() && getConfiguration().isIgnoredInput(event.getState()) == false
    ) {
      onMotionStateWasCreated((BooleanState) event.getState());
    }
  }

  public void onMotionStateWasCreated (
    @NonNull final BooleanState created
  ) {
    if (created.getValue() == false) return;

    final List<ApplicationEntityReference<? extends Sensor>> inputSensors = getInputSensors();
    final Optional<BooleanState>                             previous     = _flags.findPreviousWithValue(created, inputSensors, true);
    final Optional<BooleanState>                             next         = _flags.findNextWithValue(created, inputSensors, true);
    
    if (previous.isPresent() == false) {
      onLeftMotionStateWasCreated(created, next);
    } else if (!areOfSameType(previous.get(), created)) {
      onRightMotionStateWasCreated(created, next);
    }
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
    
    if (event.getNewValue().getSensor().isOfType(NativeMotionSensor.class) &&
        !getConfiguration().isIgnoredInput(event.getNewValue())
    ) {
      onMotionStateWasMutated((BooleanState) event.getOldValue(), (BooleanState) event.getNewValue()
      );
    }
  }

  public void onMotionStateWasMutated (
    @NonNull final BooleanState base,
    @NonNull final BooleanState updated
  ) {
    final Optional<BooleanState> correlation = _flags.findFirstWithCorrelation("base",
                                                                               updated.getReference(),
                                                                               getSensor().getReference()
                                                                                          .as(Sensor.class)
    );
    
    if (correlation.isPresent()) {
      onCorrelledMotionStateWasMutated(base, updated);
    } else if (updated.getValue()) {
      onMotionStateWasCreated(updated);
    }
  }

  private void onCorrelledMotionStateWasMutated (
    @NonNull final BooleanState base,
    @NonNull final BooleanState updated
  ) {
    final List<ApplicationEntityReference<? extends Sensor>> inputSensors = getInputSensors();
    final Optional<BooleanState>                             previous     = _flags.findPreviousWithValue(
      base.getEmissionDate(),
      inputSensors,
      true
    );
    final Optional<BooleanState>                             next         = _flags.findNextWithValue(
      base.getEmissionDate(),
      inputSensors,
      true
    );
    
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
    if (!_flags.findFirstWithCorrelation("base", state.getReference(), getSensor().getReference().as(Sensor.class))
               .isPresent()) return;

    final List<ApplicationEntityReference<? extends Sensor>> inputs = getInputSensors();
    final Optional<BooleanState> previous = _flags.findPreviousWithValue(state.getEmissionDate(), inputs, true
    );
    
    final Optional<BooleanState> next = _flags.findNextWithValue(state.getEmissionDate(), inputs, true
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
    _schemaManager.execute(new StateDeletionSchema((State) _flags.findFirstWithCorrelation("base",
                                                                                           state.getReference(),
                                                                                           getSensor().getReference()
    ).get()));
  }

  private void move (
    @NonNull final BooleanState from, 
    @NonNull final BooleanState to
  ) {
    final State toMove = (State) _flags.findFirstWithCorrelation("base", from.getReference(), getSensor().getReference()
    ).get();

    final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema();
    mutation.setState(toMove.getReference());
    mutation.setEmittionDate(to.getEmissionDate());
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
    creation.setEmittionDate(state.getEmissionDate());
    creation.setSensor(getSensor().getReference());
    creation.setValue(up);
    creation.correlate("base", state);
    
    _schemaManager.execute(creation);
  }
}
