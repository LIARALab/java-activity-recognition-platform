package org.liara.api.recognition.sensor.montiontracking;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.LongValueState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.data.series.SeriesManager;
import org.liara.api.event.sensor.SensorWasCreatedEvent;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillDeleteStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.api.recognition.sensor.type.ValueSensorType;
import org.liara.api.utils.Duplicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MotionTrackerSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType
{
  @NonNull
  private final APIEventPublisher _apiEventPublisher;

  @NonNull
  private final BooleanValueStateRepository _booleanValues;

  @NonNull
  private final SeriesManager _seriesManager;

  @NonNull
  private final SensorRepository _sensors;

  @NonNull
  private final CorrelationRepository _correlations;

  @NonNull
  private final NodeRepository _nodes;

  @NonNull
  private final MotionTrackerSensorAsserter _asserter;

  @Autowired
  public MotionTrackerSensor (@NonNull final MotionTrackerSensorBuilder builder) {
    _correlations = Objects.requireNonNull(builder.getCorrelations());
    _apiEventPublisher = Objects.requireNonNull(builder.getApiEventPublisher());
    _booleanValues = Objects.requireNonNull(builder.getBooleanValues());
    _sensors = Objects.requireNonNull(builder.getSensors());
    _nodes = Objects.requireNonNull(builder.getNodes());
    _asserter = new MotionTrackerSensorAsserter(this);
    _seriesManager = Objects.requireNonNull(builder.getSeriesManager());
  }

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
    _asserter.refresh();

    @NonNull final List<@NonNull LongValueState> states = _seriesManager.getLongSeries(
      getConfiguration().getSource()
    ).get();

    @Nullable Boolean previous = null;

    for (@NonNegative int index = 0; index < states.size(); ++index) {
      @NonNull final LongValueState currentState = states.get(index);

      if (!_asserter.isIgnoredInput(currentState)) {
        @NonNull final Boolean current = _asserter.isValidInput(currentState);

        if (previous == null || previous != current) {
          emit(currentState);
        }

        previous = current;
      }
    }
  }

  @Override
  public void resume (@NonNull final VirtualSensorRunner runner) {
    super.resume(runner);
    _asserter.refresh();
  }

  private boolean isInputState (@NonNull final State state) {
    return Objects.equals(state.getSensorIdentifier(), getConfiguration().getSource()) &&
           !_asserter.isIgnoredInput((LongValueState) state);
  }

  @Override
  public void sensorWasCreated (@NonNull final SensorWasCreatedEvent event) {
    super.sensorWasCreated(event);

    if (event.getSensor().getTypeInstance() == ValueSensorType.MOTION) {
      _asserter.refresh();
    }
  }

  @Override
  public void stateWasCreated (@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (isInputState(stateThatWasCreated)) {
      onInputStateWasCreated((LongValueState) stateThatWasCreated);
    }
  }

  private void onInputStateWasCreated (@NonNull final LongValueState stateThatWasCreated) {
    @NonNull final Optional<LongValueState> previous = _seriesManager.getLongSeries(
      getConfiguration().getSource()
    ).getPrevious(stateThatWasCreated.getEmissionDate());

    if (!previous.isPresent()) {
      onLeftMotionStateWasCreated(stateThatWasCreated);
    } else if (!areOfSameType(previous.get(), stateThatWasCreated)) {
      onRightMotionStateWasCreated(stateThatWasCreated);
    }
  }

  private void onLeftMotionStateWasCreated (@NonNull final LongValueState stateThatWasCreated) {
    @NonNull final Optional<LongValueState> next = _seriesManager.getLongSeries(
      getConfiguration().getSource()
    ).getNext(stateThatWasCreated.getEmissionDate());

    if (next.isPresent() && areOfSameType(stateThatWasCreated, next.get())) {
      move(next.get(), stateThatWasCreated);
    } else {
      emit(stateThatWasCreated);
    }
  }

  private void onRightMotionStateWasCreated (@NonNull final LongValueState stateThatWasCreated) {
    @NonNull final Optional<LongValueState> next = _seriesManager.getLongSeries(
      getConfiguration().getSource()
    ).getNext(stateThatWasCreated.getEmissionDate());

    if (!next.isPresent()) {
      emit(stateThatWasCreated);
    } else if (areOfSameType(stateThatWasCreated, next.get())) {
      move(next.get(), stateThatWasCreated);
    } else {
      emit(stateThatWasCreated);
      emit(next.get());
    }
  }

  @Override
  public void stateWillBeMutated (@NonNull final WillUpdateStateEvent event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (isInputState(stateThatWillBeMutated)) {
      onInputStateWillBeMutated((LongValueState) stateThatWillBeMutated);
    }
  }

  private void onInputStateWillBeMutated (@NonNull final LongValueState stateThatWillBeMutated) {
    onInputStateWillBeDeleted(stateThatWillBeMutated);
  }

  @Override
  public void stateWasMutated (@NonNull final DidUpdateStateEvent event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (isInputState(stateThatWasMutated)) {
      onInputStateWasMutated((LongValueState) stateThatWasMutated);
    }
  }

  public void onInputStateWasMutated (@NonNull final LongValueState stateThatWasMutated) {
    onInputStateWasCreated(stateThatWasMutated);
  }

  @Override
  public void stateWillBeDeleted (@NonNull final WillDeleteStateEvent event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (isInputState(stateThatWillBeDeleted)) {
      onInputStateWillBeDeleted((LongValueState) stateThatWillBeDeleted);
    }
  }

  public void onInputStateWillBeDeleted (@NonNull final LongValueState stateThatWillBeDeleted) {
    Objects.requireNonNull(stateThatWillBeDeleted.getIdentifier());
    Objects.requireNonNull(stateThatWillBeDeleted.getEmissionDate());

    if (!findCorrelationFromInput(stateThatWillBeDeleted.getIdentifier()).isPresent()) return;

    @NonNull final Optional<LongValueState> previous = _seriesManager.getLongSeries(
      getConfiguration().getSource()
    ).getPrevious(stateThatWillBeDeleted.getEmissionDate());

    @NonNull final Optional<LongValueState> next = _seriesManager.getLongSeries(
      getConfiguration().getSource()
    ).getNext(stateThatWillBeDeleted.getEmissionDate());

    if (!previous.isPresent()) {
      onLeftMotionStateWillBeDeleted(stateThatWillBeDeleted, next.orElse(null));
    } else {
      onRightMotionStateWillBeDeleted(stateThatWillBeDeleted, next.orElse(null));
    }
  }

  private void onRightMotionStateWillBeDeleted (
    @NonNull final LongValueState stateThatWillBeDeleted,
    @Nullable final LongValueState next
  ) {
    if (next == null) {
      delete(stateThatWillBeDeleted);
    } else if (areOfSameType(stateThatWillBeDeleted, next)) {
      move(stateThatWillBeDeleted, next);
    } else {
      delete(stateThatWillBeDeleted);
      delete(next);
    }
  }

  private void onLeftMotionStateWillBeDeleted (
    @NonNull final LongValueState stateThatWillBeDeleted,
    @Nullable final LongValueState next
  ) {
    if (next == null) {
      delete(stateThatWillBeDeleted);
    } else if (areOfSameType(stateThatWillBeDeleted, next)) {
      move(stateThatWillBeDeleted, next);
    } else {
      delete(stateThatWillBeDeleted);
    }
  }

  private @NonNull Optional<Correlation> findCorrelationFromInput (@NonNull final Long input) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().map(Sensor::getIdentifier).orElseThrow(), "origin", input
    );
  }

  private @NonNull Optional<Correlation> findCorrelationFromOutput (@NonNull final Long output) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatStartBy(
      getSensor().map(Sensor::getIdentifier).orElseThrow(), "origin", output
    );
  }

  private @NonNull Optional<BooleanValueState> findOutput (@NonNull final Long input) {
    return findCorrelationFromInput(input).map(Correlation::getStartStateIdentifier)
                                          .flatMap(_booleanValues::find);
  }

  private void delete (@NonNull final LongValueState state) {
    Objects.requireNonNull(state.getIdentifier());

    @NonNull final Correlation correlation = (
      findCorrelationFromInput(state.getIdentifier()).orElseThrow()
    );

    @NonNull final BooleanValueState output = (
      _booleanValues.find(correlation.getStartStateIdentifier()).orElseThrow()
    );

    _apiEventPublisher.delete(correlation, output);
  }

  private void move (@NonNull final LongValueState from, @NonNull final LongValueState to) {
    Objects.requireNonNull(from.getIdentifier());

    @NonNull final BooleanValueState toMove = (
      findOutput(from.getIdentifier()).map(Duplicator::duplicate).orElseThrow()
    );

    toMove.setEmissionDate(to.getEmissionDate());

    if (Objects.equals(from, to)) {
      _apiEventPublisher.update(toMove);
    } else {
      Objects.requireNonNull(toMove.getIdentifier());

      @NonNull final Correlation correlation = (
        findCorrelationFromOutput(toMove.getIdentifier()).map(Duplicator::duplicate).orElseThrow()
      );

      correlation.setEndStateIdentifier(to.getIdentifier());

      _apiEventPublisher.update(toMove, correlation);
    }
  }

  private void emit (@NonNull final LongValueState input) {
    emit(input, _asserter.isValidInput(input));
  }

  private void emit (@NonNull final State input, final boolean up) {
    @NonNull final BooleanValueState output = new BooleanValueState();

    output.setEmissionDate(input.getEmissionDate());
    output.setSensorIdentifier(getSensor().map(Sensor::getIdentifier).orElseThrow());
    output.setValue(up);

    _apiEventPublisher.create(output);

    @NonNull final Correlation correlation = new Correlation();

    correlation.setStartStateIdentifier(output.getIdentifier());
    correlation.setEndStateIdentifier(input.getIdentifier());
    correlation.setName("origin");

    _apiEventPublisher.create(correlation);
  }

  private boolean areOfSameType (
    @NonNull final LongValueState left,
    @NonNull final LongValueState right
  ) {
    return _asserter.isValidInput(left) == _asserter.isValidInput(right);
  }

  public @NonNull MotionTrackerSensorConfiguration getConfiguration () {
    return getConfiguration(MotionTrackerSensorConfiguration.class).orElseThrow();
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return BooleanValueState.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return MotionTrackerSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:motion-tracker";
  }

  public @NonNull APIEventPublisher getApiEventPublisher () {
    return _apiEventPublisher;
  }

  public @NonNull BooleanValueStateRepository getBooleanValues () {
    return _booleanValues;
  }

  public @NonNull SensorRepository getSensors () {
    return _sensors;
  }

  public @NonNull CorrelationRepository getCorrelations () {
    return _correlations;
  }

  public @NonNull NodeRepository getNodes () {
    return _nodes;
  }
}
