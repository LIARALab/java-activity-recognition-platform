package org.liara.api.recognition.sensor.motionmapping;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.*;
import org.liara.api.event.sensor.DidCreateSensorEvent;
import org.liara.api.event.sensor.DidDeleteSensorEvent;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillDeleteStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MotionMapperSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType
{
  @NonNull
  private final APIEventPublisher _apiEventPublisher;

  @NonNull
  private final AnyStateRepository _booleanValues;

  @NonNull
  private final LongValueStateRepository _longValues;

  @NonNull
  private final SensorRepository _sensors;

  @NonNull
  private final CorrelationRepository _correlations;

  @NonNull
  private final NodeRepository _nodes;

  @NonNull
  private final MotionMapperSensorAsserter _asserter;

  @Autowired
  public MotionMapperSensor (@NonNull final MotionMapperSensorBuilder builder) {
    _correlations = Objects.requireNonNull(builder.getCorrelations());
    _apiEventPublisher = Objects.requireNonNull(builder.getApiEventPublisher());
    _sensors = Objects.requireNonNull(builder.getSensors());
    _nodes = Objects.requireNonNull(builder.getNodes());
    _asserter = new MotionMapperSensorAsserter(this);
    _booleanValues = Objects.requireNonNull(builder.getBooleanValues());
    _longValues = Objects.requireNonNull(builder.getLongValues());
  }

  private @NonNull BooleanValueState cast (@NonNull final State state) {
    if (state instanceof BooleanValueState) {
      return (BooleanValueState) state;
    } else if (state instanceof NumericValueState) {
      return new BooleanValueState((NumericValueState<? extends Number>) state);
    } else {
      throw new Error("Invalid input state type : " + state.getClass().getName() + ".");
    }
  }

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
    _asserter.refresh();

    @NonNull final List<@NonNull State> states = _booleanValues.find(
      _asserter.getTrackedSensors(), Cursor.ALL
    );

    for (@NonNull final State state : states) {
      @NonNull final BooleanValueState flag = cast(state);

      if (flag.requireValue()) emit(flag);
    }
  }

  @Override
  public void resume (@NonNull final VirtualSensorRunner runner) {
    super.resume(runner);
    _asserter.refresh();
  }

  private boolean isInputState (@NonNull final State state) {
    if (_asserter.isTracked(state)) {
      return Objects.equals(cast(state).getValue(), true);
    }

    return false;
  }

  @Override
  public void sensorWasCreated (@NonNull final DidCreateSensorEvent event) {
    super.sensorWasCreated(event);
    _asserter.refresh();
  }

  @Override
  public void sensorWasDeleted (@NonNull final DidDeleteSensorEvent event) {
    super.sensorWasDeleted(event);
    _asserter.refresh();
  }

  @Override
  public void stateWasCreated (@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (isInputState(stateThatWasCreated)) {
      emit(stateThatWasCreated);
    }
  }

  @Override
  public void stateWillBeMutated (@NonNull final WillUpdateStateEvent event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (isInputState(stateThatWillBeMutated)) {
      delete(stateThatWillBeMutated);
    }
  }

  @Override
  public void stateWasMutated (@NonNull final DidUpdateStateEvent event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (isInputState(stateThatWasMutated)) {
      emit(stateThatWasMutated);
    }
  }

  @Override
  public void stateWillBeDeleted (@NonNull final WillDeleteStateEvent event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (isInputState(stateThatWillBeDeleted)) {
      delete(stateThatWillBeDeleted);
    }
  }

  private @NonNull Optional<Correlation> findCorrelationFromInput (@NonNull final Long input) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().map(Sensor::getIdentifier).orElseThrow(), "origin", input
    );
  }

  private @NonNull Optional<LongValueState> findOutput (@NonNull final Long input) {
    return findCorrelationFromInput(input).map(Correlation::getStartStateIdentifier)
                                          .flatMap(_longValues::find);
  }

  private void delete (@NonNull final State state) {
    Objects.requireNonNull(state.getIdentifier());

    @NonNull final Correlation correlation = (
      findCorrelationFromInput(state.getIdentifier()).orElseThrow()
    );

    @NonNull final LongValueState output = (
      _longValues.find(correlation.getStartStateIdentifier()).orElseThrow()
    );

    _apiEventPublisher.delete(correlation, output);
  }

  private void emit (@NonNull final State input) {
    @NonNull final LongValueState output = new LongValueState();

    output.setEmissionDate(input.getEmissionDate());
    output.setSensorIdentifier(getSensor().map(Sensor::getIdentifier).orElseThrow());
    output.setValue(input.getSensorIdentifier());

    _apiEventPublisher.create(output);

    @NonNull final Correlation correlation = new Correlation();

    correlation.setStartStateIdentifier(output.getIdentifier());
    correlation.setEndStateIdentifier(input.getIdentifier());
    correlation.setName("origin");

    _apiEventPublisher.create(correlation);
  }

  public @NonNull MotionMapperSensorConfiguration getConfiguration () {
    return getConfiguration(MotionMapperSensorConfiguration.class).orElseThrow();
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return LongValueState.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return MotionMapperSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:motion-mapper";
  }

  public @NonNull APIEventPublisher getApiEventPublisher () {
    return _apiEventPublisher;
  }

  public @NonNull AnyStateRepository getBooleanValues () {
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
