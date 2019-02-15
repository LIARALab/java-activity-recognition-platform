package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SapaRepositories;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.api.recognition.sensor.type.ValueSensorType;
import org.liara.api.utils.Duplicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope("prototype")
public class OneVsAllToUpDownMotionSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  private final SapaRepositories.@NonNull Boolean _flags;

  @NonNull
  private final SensorRepository _sensors;

  @NonNull
  private final CorrelationRepository _correlations;

  @NonNull
  private final NodeRepository _nodes;

  @Autowired
  public OneVsAllToUpDownMotionSensor (
    @NonNull final ApplicationEventPublisher publisher,
    final SapaRepositories.@NonNull Boolean flags,
    @NonNull final SensorRepository sensors,
    @NonNull final CorrelationRepository correlations,
    @NonNull final NodeRepository nodes
  )
  {
    _correlations = correlations;
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

  public @NonNull List<@NonNull Long> getInputSensors () {
    final @NonNull List<@NonNull Long> result = new ArrayList<>();

    _sensors.getSensorsOfTypeIntoNode(
      ValueSensorType.MOTION.getName(),
      _nodes.getRoot(_nodes.find(getSensor().getNodeIdentifier()).get()).getIdentifier()
    )
            .stream()
            .filter((@NonNull final Sensor sensor) -> !getConfiguration().isIgnoredInput(sensor))
      .forEach((@NonNull final Sensor sensor) -> result.add(sensor.getIdentifier()));

    return result;
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  )
  {
    super.initialize(runner);

    @NonNull final List<ValueState.@NonNull Boolean> states = _flags.findAllWithValue(getInputSensors(), true);

    @NonNull final OneVsAllToUpDownMotionSensorConfiguration configuration = getConfiguration();

    ValueState.@Nullable Boolean previous = null;

    for (int index = 0; index < states.size(); ++index) {
      final ValueState.@NonNull Boolean current = states.get(index);

      if (previous == null || areOfSameType(previous, current) == false) {
        emit(current, configuration.isValidInput(current));
      }

      previous = current;
    }
  }

  @Override
  public void stateWasCreated (
    final StateEvent.@NonNull WasCreated event
  )
  {
    super.stateWasCreated(event);

    @NonNull final Sensor sensor = _sensors.find(event.getState().getSensorIdentifier()).get();

    if (sensor.getTypeInstance() == ValueSensorType.MOTION &&
        ((ValueState.Boolean) event.getState()).getValue() &&
        getConfiguration().isIgnoredInput(event.getState()) == false) {
      onMotionStateWasCreated((ValueState.Boolean) event.getState());
    }
  }

  public void onMotionStateWasCreated (
    final ValueState.@NonNull Boolean created
  )
  {
    @NonNull final List<@NonNull Long> inputSensors = getInputSensors();
    @NonNull final Optional<ValueState.Boolean> previous =
      _flags.findPreviousWithValue(
      created,
      inputSensors,
      true
    );
    @NonNull final Optional<ValueState.Boolean> next = _flags.findNextWithValue(
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
    final ValueState.@NonNull Boolean created, @NonNull final Optional<ValueState.Boolean> next
  )
  {
    if (next.isPresent() && areOfSameType(created, next.get())) {
      move(next.get(), created);
    } else {
      emit(created);
    }
  }

  private void onRightMotionStateWasCreated (
    final ValueState.@NonNull Boolean created, @NonNull final Optional<ValueState.Boolean> next
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
    final ValueState.@NonNull Boolean left, final ValueState.@NonNull Boolean right
  )
  {
    return getConfiguration().isValidInput(left) == getConfiguration().isValidInput(right);
  }

  @Override
  public void stateWasMutated (
    final StateEvent.@NonNull WasMutated event
  )
  {
    super.stateWasMutated(event);

    @NonNull final Sensor sensor = _sensors.find(event.getNewValue().getSensorIdentifier()).get();

    if (sensor.getTypeInstance() == ValueSensorType.MOTION &&
        !getConfiguration().isIgnoredInput(event.getNewValue())) {
      onMotionStateWasMutated((ValueState.Boolean) event.getOldValue(), (ValueState.Boolean) event.getNewValue());
    }
  }

  public void onMotionStateWasMutated (
    final ValueState.@NonNull Boolean base, final ValueState.@NonNull Boolean updated
  )
  {
    @NonNull final Optional<ValueState.Boolean> correlation = findRelatedResult(updated.getIdentifier());

    if (correlation.isPresent()) {
      onCorrelledMotionStateWasMutated(base, updated);
    } else if (updated.getValue()) {
      onMotionStateWasCreated(updated);
    }
  }

  private @NonNull Optional<ValueState.@NonNull Boolean> findRelatedResult (
    @NonNull final Long reference
  )
  {
    @NonNull final Optional<Correlation> correlation =
      _correlations.findFirstCorrelationFromSeriesWithNameAndThatStartBy(
        getSensor().getIdentifier(),
        "origin",
        reference
    );

    if (correlation.isPresent()) {
      return _flags.find(correlation.get().getStartStateIdentifier());
    } else {
      return Optional.empty();
    }
  }

  private void onCorrelledMotionStateWasMutated (
    final ValueState.@NonNull Boolean base, final ValueState.@NonNull Boolean updated
  )
  {
    @NonNull final List<@NonNull Long> inputSensors = getInputSensors();
    @NonNull final Optional<ValueState.Boolean> previous = _flags.findPreviousWithValue(
      base.getEmissionDate(),
                                                                                         inputSensors,
                                                                                         true
    );
    @NonNull final Optional<ValueState.Boolean> next = _flags.findNextWithValue(
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
    final ValueState.@NonNull Boolean updated, @NonNull final Optional<ValueState.Boolean> next
  )
  {
    if (!next.isPresent()) {
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
    final StateEvent.@NonNull WillBeDeleted event
  )
  {
    super.stateWillBeDeleted(event);

    if (event.getState() instanceof ValueState.Boolean) {
      final ValueState.@NonNull Boolean state  = (ValueState.Boolean) event.getState();
      @NonNull final Sensor             sensor = _sensors.find(state.getSensorIdentifier())
                                                   .orElseThrow();

      if (sensor.getTypeInstance() == ValueSensorType.MOTION && state.getValue()) {
        onMotionStateWillBeDeleted(state);
      }
    }
  }

  public void onMotionStateWillBeDeleted (final ValueState.@NonNull Boolean state) {
    if (!findRelatedResult(state.getIdentifier()).isPresent()) return;

    final List<Long> inputs = getInputSensors();
    final Optional<ValueState.Boolean> previous = _flags.findPreviousWithValue(
      state.getEmissionDate(),
      inputs,
      true
    );

    final Optional<ValueState.Boolean> next = _flags.findNextWithValue(state.getEmissionDate(), inputs, true);

    if (previous.isPresent() == false) {
      onLeftMotionStateWillBeDeleted(state, next);
    } else {
      onRightMotionStateWillBeDeleted(state, next);
    }
  }

  private void onRightMotionStateWillBeDeleted (
    final ValueState.@NonNull Boolean state, @NonNull final Optional<ValueState.Boolean> next
  )
  {
    if (!next.isPresent()) {
      delete(state);
    } else if (areOfSameType(state, next.get())) {
      move(state, next.get());
    } else {
      delete(state);
      delete(next.get());
    }
  }

  private void onLeftMotionStateWillBeDeleted (
    final ValueState.@NonNull Boolean state, @NonNull final Optional<ValueState.Boolean> next
  )
  {
    if (!next.isPresent()) {
      delete(state);
    } else if (areOfSameType(state, next.get())) {
      move(state, next.get());
    } else {
      delete(state);
    }
  }

  private void delete (final ValueState.@NonNull Boolean state) {
    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Delete(
      this,
      findRelatedResult(state.getIdentifier()).get()
    ));
  }

  private void move (
    final ValueState.@NonNull Boolean from, final ValueState.@NonNull Boolean to
  )
  {
    final ValueState.@NonNull Boolean toMove = Duplicator.duplicate(findRelatedResult(from.getIdentifier()).get());
    toMove.setEmissionDate(to.getEmissionDate());

    if (!Objects.equals(from, to)) {
      @NonNull final Correlation correlation = Duplicator.duplicate(_correlations
                                                                      .findFirstCorrelationFromSeriesWithNameAndThatStartBy(
                                                                        getSensor().getIdentifier(),
                                                                        "origin",
                                                                        toMove.getIdentifier()
      ).get());
      correlation.setEndStateIdentifier(to.getIdentifier());

      _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Update(this, toMove, correlation));
    } else {
      _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Update(this, toMove));
    }
  }

  private void emit (final ValueState.@NonNull Boolean created) {
    emit(created, getConfiguration().isValidInput(created));
  }

  private void emit (@NonNull final State state, final boolean up) {
    final ValueState.@NonNull Boolean flag = new ValueState.Boolean();

    flag.setEmissionDate(state.getEmissionDate());
    flag.setSensorIdentifier(getSensor().getIdentifier());
    flag.setValue(up);

    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, flag));

    @NonNull final Correlation correlation = new Correlation();

    correlation.setStartStateIdentifier(flag.getIdentifier());
    correlation.setEndStateIdentifier(state.getIdentifier());
    correlation.setName("origin");

    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, correlation));
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return ValueState.Boolean.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return OneVsAllToUpDownMotionSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:onevsall";
  }
}
