package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@UseSensorConfigurationOfType(OneVsAllToUpDownMotionSensorConfiguration.class)
@EmitStateOfType(ValueState.Boolean.class)
@Component
@Scope("prototype")
public class OneVsAllToUpDownMotionSensor
  extends AbstractVirtualSensorHandler
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @NonNull
  private final ValueStateRepository<Boolean> _flags;

  @NonNull
  private final SensorRepository _sensors;

  @NonNull
  private final NodeRepository _nodes;

  @Autowired
  public OneVsAllToUpDownMotionSensor (
    @NonNull final ApplicationEventPublisher publisher, @NonNull final ValueStateRepository<Boolean> flags,
    @NonNull final SensorRepository sensors,
    @NonNull final NodeRepository nodes
  )
  {
    _applicationEventPublisher = publisher;
    _flags = flags;
    _sensors = sensors;
    _nodes = nodes;
  }

  public @NonNull OneVsAllToUpDownMotionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(OneVsAllToUpDownMotionSensorConfiguration.class);
  }

  public @NonNull Sensor getSensor () {
    return getRunner().getSensor();
  }

  public @NonNull List<@NonNull ApplicationEntityReference<? extends Sensor>> getInputSensors () {
    final @NonNull List<@NonNull ApplicationEntityReference<? extends Sensor>> result = new ArrayList<>();

    _sensors.getSensorsOfTypeIntoNode(NativeMotionSensor.class,
                                      _nodes.getRoot(_nodes.find(getSensor().getNodeIdentifier()).get()).getReference()
    )
            .stream()
            .filter((@NonNull final Sensor sensor) -> !getConfiguration().isIgnoredInput(sensor))
            .forEach((@NonNull final Sensor sensor) -> result.add(sensor.getReference()));

    return result;
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  )
  {
    super.initialize(runner);

    @NonNull final List<@NonNull ValueState<Boolean>> states = _flags.findAllWithValue(getInputSensors(), true);

    @NonNull final OneVsAllToUpDownMotionSensorConfiguration configuration = getConfiguration();

    @Nullable ValueState<Boolean> previous = null;

    for (int index = 0; index < states.size(); ++index) {
      @NonNull final ValueState<Boolean> current = states.get(index);

      if (previous == null || areOfSameType(previous, current) == false) {
        emit(current, configuration.isValidInput(current));
      }

      previous = current;
    }
  }

  @Override
  public void stateWasCreated (
    @NonNull final StateEvent.WasCreated event
  )
  {
    super.stateWasCreated(event);

    @NonNull final Sensor sensor = _sensors.find(event.getState().getSensorIdentifier()).get();

    if (sensor.isOfType(NativeMotionSensor.class) && ((ValueState<Boolean>) event.getState()).getValue() &&
        getConfiguration().isIgnoredInput(event.getState()) == false) {
      onMotionStateWasCreated((ValueState<Boolean>) event.getState());
    }
  }

  public void onMotionStateWasCreated (
    @NonNull final ValueState<Boolean> created
  )
  {
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors = getInputSensors();
    @NonNull final Optional<ValueState<Boolean>>                               previous     =
      _flags.findPreviousWithValue(
      created,
      inputSensors,
      true
    );
    @NonNull final Optional<ValueState<Boolean>>                               next         = _flags.findNextWithValue(
      created,
      inputSensors,
      true
    );

    if (previous.isPresent() == false) {
      onLeftMotionStateWasCreated(created, next);
    } else if (!areOfSameType(previous.get(), created)) {
      onRightMotionStateWasCreated(created, next);
    }
  }

  private void onLeftMotionStateWasCreated (
    @NonNull final ValueState<Boolean> created, @NonNull final Optional<ValueState<Boolean>> next
  )
  {
    if (next.isPresent() && areOfSameType(created, next.get())) {
      move(next.get(), created);
    } else {
      emit(created);
    }
  }

  private void onRightMotionStateWasCreated (
    @NonNull final ValueState<Boolean> created, @NonNull final Optional<ValueState<Boolean>> next
  )
  {
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
    @NonNull final ValueState<Boolean> left, @NonNull final ValueState<Boolean> right
  )
  {
    return getConfiguration().isValidInput(left) == getConfiguration().isValidInput(right);
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateEvent.WasMutated event
  )
  {
    super.stateWasMutated(event);

    @NonNull final Sensor sensor = _sensors.find(event.getNewValue().getSensorIdentifier()).get();

    if (sensor.isOfType(NativeMotionSensor.class) && !getConfiguration().isIgnoredInput(event.getNewValue())) {
      onMotionStateWasMutated((ValueState<Boolean>) event.getOldValue(), (ValueState<Boolean>) event.getNewValue());
    }
  }

  public void onMotionStateWasMutated (
    @NonNull final ValueState<Boolean> base, @NonNull final ValueState<Boolean> updated
  )
  {
    @NonNull final Optional<ValueState<Boolean>> correlation = _flags.findFirstWithCorrelation("base",
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
    @NonNull final ValueState<Boolean> base, @NonNull final ValueState<Boolean> updated
  )
  {
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors = getInputSensors();
    @NonNull final Optional<ValueState<Boolean>> previous = _flags.findPreviousWithValue(base.getEmissionDate(),
                                                                                         inputSensors,
                                                                                         true
    );
    @NonNull final Optional<ValueState<Boolean>> next = _flags.findNextWithValue(base.getEmissionDate(),
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
    @NonNull final ValueState<Boolean> updated, @NonNull final Optional<ValueState<Boolean>> next
  )
  {
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
    @NonNull final StateEvent.WillBeDeleted event
  )
  {
    super.stateWillBeDeleted(event);

    if (event.getState() instanceof ValueState.Boolean) {
      @NonNull final ValueState<Boolean> state  = (ValueState<Boolean>) event.getState();
      @NonNull final Sensor              sensor = _sensors.find(state.getSensorIdentifier()).get();

      if (sensor.isOfType(NativeMotionSensor.class) && state.getValue()) {
        onMotionStateWillBeDeleted(state);
      }
    }
  }

  public void onMotionStateWillBeDeleted (@NonNull final ValueState<Boolean> state) {
    if (!_flags.findFirstWithCorrelation("base", state.getReference(), getSensor().getReference().as(Sensor.class))
               .isPresent()) return;

    final List<ApplicationEntityReference<? extends Sensor>> inputs   = getInputSensors();
    final Optional<ValueState<Boolean>>                      previous = _flags.findPreviousWithValue(
      state.getEmissionDate(),
      inputs,
      true
    );

    final Optional<ValueState<Boolean>> next = _flags.findNextWithValue(state.getEmissionDate(), inputs, true);

    if (previous.isPresent() == false) {
      onLeftMotionStateWillBeDeleted(state, next);
    } else {
      onRightMotionStateWillBeDeleted(state, next);
    }
  }

  private void onRightMotionStateWillBeDeleted (
    @NonNull final ValueState<Boolean> state, @NonNull final Optional<ValueState<Boolean>> next
  )
  {
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
    @NonNull final ValueState<Boolean> state, @NonNull final Optional<ValueState<Boolean>> next
  )
  {
    if (next.isPresent() == false) {
      delete(state);
    } else if (areOfSameType(state, next.get())) {
      move(state, next.get());
    } else {
      delete(state);
    }
  }

  private void delete (@NonNull final ValueState<Boolean> state) {
    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Delete(this,
                                                                              _flags.findFirstWithCorrelation("base",
                                                                                                              state.getReference(),
                                                                                                              getSensor()
                                                                                                                .getReference()
                                                                              ).get()
    ));
  }

  private void move (
    @NonNull final ValueState<Boolean> from, @NonNull final ValueState<Boolean> to
  )
  {
    @NonNull final State toMove = (State) _flags.findFirstWithCorrelation("base",
                                                                          from.getReference(),
                                                                          getSensor().getReference()
    ).get();

    final ValueState.BooleanMutationSchema mutation = new ValueState.BooleanMutationSchema();
    mutation.setState(toMove.getReference());
    mutation.setEmittionDate(to.getEmissionDate());
    if (Objects.equals(from, to) == false) {
      mutation.correlate("base", to);
    }
    _schemaManager.execute(mutation);
  }

  private void emit (@NonNull final ValueState<Boolean> created) {
    emit(created, getConfiguration().isValidInput(created));
  }

  private void emit (@NonNull final State state, final boolean up) {
    @NonNull final ValueState.Boolean flag = new ValueState.Boolean();

    flag.setEmissionDate(state.getEmissionDate());
    flag.setSensorIdentifier(getSensor().getReference());
    flag.setValue(up);

    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, flag));

    creation.correlate("base", state);
  }
}
