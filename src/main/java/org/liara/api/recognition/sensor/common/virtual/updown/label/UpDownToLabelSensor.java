package org.liara.api.recognition.sensor.common.virtual.updown.label;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.LabelStateRepository;
import org.liara.api.data.repository.SapaRepositories;
import org.liara.api.event.StateEvent;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope("prototype")
public class UpDownToLabelSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType
{
  @NonNull
  private final APIEventPublisher _publisher;

  @NonNull
  private final LabelStateRepository _outputs;

  private final SapaRepositories.@NonNull Boolean _inputs;

  @NonNull
  private final CorrelationRepository _correlations;

  @Autowired
  public UpDownToLabelSensor (@NonNull final UpDownToLabelSensorBuilder builder) {
    _publisher = Objects.requireNonNull(builder.getPublisher());
    _inputs = Objects.requireNonNull(builder.getInputs());
    _outputs = Objects.requireNonNull(builder.getOutputs());
    _correlations = Objects.requireNonNull(builder.getCorrelations());
  }

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);

    @NonNull final List<BooleanValueState> initializationStates = _inputs.find(
      getInputSensorIdentifier(), Cursor.ALL);
    LabelState                                       current              = null;

    for (int index = 0; index < initializationStates.size(); ++index) {
      current = initialize(current, initializationStates.get(index));
    }
  }

  private @Nullable LabelState initialize (
    @Nullable final LabelState current,
    final BooleanValueState next
  )
  {
    Objects.requireNonNull(next.getValue());

    if (next.getValue() && current == null) {
      return begin(next);
    } else if (!next.getValue() && current != null) {
      finish(current, next);
      return null;
    } else {
      return current;
    }
  }

  @Override
  public void stateWasCreated (final StateEvent.@NonNull WasCreated event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (Objects.equals(stateThatWasCreated.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWasCreated((BooleanValueState) stateThatWasCreated);
    }
  }

  private void inputStateWasCreated (final BooleanValueState stateThatWasCreated) {
    Objects.requireNonNull(stateThatWasCreated.getEmissionDate());

    _outputs.findAt(
      stateThatWasCreated.getEmissionDate(),
      getOutputSensorIdentifier()
    ).ifPresentOrElse(
      (parentLabel) -> innerInputStateWasCreated(parentLabel, stateThatWasCreated),
      () -> outerInputStateWasCreated(stateThatWasCreated)
    );
  }

  /**
   * An input state was created out of any existing labels.
   */
  private void outerInputStateWasCreated (final BooleanValueState stateThatWasCreated) {
    Objects.requireNonNull(stateThatWasCreated.getValue());

    if (!stateThatWasCreated.getValue()) return;

    _inputs.findNext(stateThatWasCreated).ifPresentOrElse(
      (next) -> outerInputStateWasCreatedBeforeAnotherState(stateThatWasCreated, next),
      () -> begin(stateThatWasCreated)
    );
  }

  private void outerInputStateWasCreatedBeforeAnotherState (
    @NonNull final BooleanValueState stateThatWasCreated,
    @NonNull final BooleanValueState next
  ) {
    Objects.requireNonNull(next.getValue());
    Objects.requireNonNull(next.getIdentifier());

    if (next.getValue()) {
      begin(
        stateThatWasCreated,
        findLabelStateThatStartBy(next.getIdentifier()).orElseThrow()
      );
    } else {
      create(stateThatWasCreated, next);
    }
  }

  /**
   * An input state was created into an existing label.
   */
  private void innerInputStateWasCreated (
    @NonNull final LabelState parentLabel,
    final BooleanValueState stateThatWasCreated
  ) {
    Objects.requireNonNull(stateThatWasCreated.getValue());

    if (stateThatWasCreated.getValue()) return;

    @NonNull final Optional<BooleanValueState> endState = getEnd(parentLabel);
    @NonNull final Optional<BooleanValueState> nextState = _inputs.findNext(stateThatWasCreated);

    finish(parentLabel, stateThatWasCreated);

    if (nextState.isPresent() && nextState.map(BooleanValueState::getValue).orElseThrow()) {
      create(nextState.get(), endState.orElse(null));
    }
  }

  @Override
  public void stateWillBeMutated (final StateEvent.@NonNull WillBeMutated event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (Objects.equals(stateThatWillBeMutated.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWillBeDeleted((BooleanValueState) stateThatWillBeMutated);
    }
  }


  @Override
  public void stateWasMutated (final StateEvent.@NonNull WasMutated event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (Objects.equals(stateThatWasMutated.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWasCreated((BooleanValueState) stateThatWasMutated);
    }
  }

  @Override
  public void stateWillBeDeleted (final StateEvent.@NonNull WillBeDeleted event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (Objects.equals(stateThatWillBeDeleted.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWillBeDeleted((BooleanValueState) stateThatWillBeDeleted);
    }
  }

  private void inputStateWillBeDeleted (final BooleanValueState stateThatWillBeDeleted) {
    Objects.requireNonNull(stateThatWillBeDeleted.getIdentifier());

    findLabelStateThatStartBy(stateThatWillBeDeleted.getIdentifier()).ifPresentOrElse(
      (parentState) -> startBoundaryWillBeDeleted(parentState, stateThatWillBeDeleted),
      () -> findLabelStateThatEndBy(stateThatWillBeDeleted.getIdentifier()).ifPresent(
        (parentState) -> endBoundaryWillBeDeleted(parentState, stateThatWillBeDeleted)
      )
    );
  }

  private void startBoundaryWillBeDeleted (
    @NonNull final LabelState label,
    final BooleanValueState boundaryThatWillBeDeleted
  ) {
    @NonNull final Optional<BooleanValueState> next = _inputs.findNext(boundaryThatWillBeDeleted);

    if (next.isPresent() && next.map(BooleanValueState::getValue).orElseThrow()) {
      begin(next.get(), label);
    } else {
      delete(label);
    }
  }

  private void endBoundaryWillBeDeleted (
    @NonNull final LabelState label,
    final BooleanValueState boundaryThatWillBeDeleted
  ) {
    _inputs.findNext(boundaryThatWillBeDeleted).ifPresentOrElse(
      (next) -> endBoundaryWillBeDeletedBeforeAnotherState(label, next),
      () -> finish(label, null)
    );
  }

  private void endBoundaryWillBeDeletedBeforeAnotherState (
    @NonNull final LabelState label,
    final BooleanValueState next
  ) {
    Objects.requireNonNull(next.getValue());
    Objects.requireNonNull(next.getIdentifier());

    if (next.getValue()) {
      merge(label, findLabelStateThatStartBy(next.getIdentifier()).orElseThrow());
    } else {
      finish(label, next);
    }
  }

  private void merge (
    @NonNull final LabelState left,
    @NonNull final LabelState right
  ) {
    @NonNull final Optional<BooleanValueState> end = getEnd(right);

    delete(right);
    finish(left, end.orElse(null));
  }

  private void delete (@NonNull final LabelState toDelete) {
    _publisher.delete(toDelete);
    _publisher.delete(getStartCorrelation(toDelete));
    getEndCorrelation(toDelete).ifPresent(_publisher::delete);
  }

  private void finish (
    @NonNull final LabelState current,
    @Nullable final State next
  ) {
    @NonNull final LabelState mutation = Duplicator.duplicate(current);

    mutation.setEnd(next == null ? null : next.getEmissionDate());

    _publisher.update(mutation);

    @NonNull final Optional<Correlation> endCorrelation = getEndCorrelation(current);

    if (next == null) {
      endCorrelation.ifPresent(_publisher::delete);
    } else if (endCorrelation.isPresent()) {
      @NonNull final Correlation correlation = Duplicator.duplicate(endCorrelation.get());
      correlation.setEndStateIdentifier(next.getIdentifier());
      _publisher.update(correlation);
    } else {
      @NonNull final Correlation correlation = new Correlation();
      correlation.setName("end");
      correlation.setEndStateIdentifier(next.getIdentifier());
      correlation.setStartStateIdentifier(current.getIdentifier());
      _publisher.create(correlation);
    }
  }

  private @NonNull LabelState begin (@NonNull final State start) {
    return create(start, null);
  }

  private @NonNull LabelState create (
    @NonNull final State start,
    @Nullable final State end
  )
  {
    @NonNull final LabelState state = new LabelState();
    state.setName(getConfiguration().getLabel());
    state.setEmissionDate(start.getEmissionDate());
    state.setSensorIdentifier(getSensor().map(Sensor::getIdentifier).orElseThrow());
    state.setStart(start.getEmissionDate());
    state.setEnd(end == null ? null : end.getEmissionDate());

    _publisher.create(state);

    @NonNull final Correlation startCorrelation = new Correlation();

    startCorrelation.setName("start");
    startCorrelation.setStartStateIdentifier(state.getIdentifier());
    startCorrelation.setEndStateIdentifier(start.getIdentifier());

    _publisher.create(startCorrelation);

    if (end != null) {
      @NonNull final Correlation endCorrelation = new Correlation();

      endCorrelation.setName("end");
      endCorrelation.setStartStateIdentifier(state.getIdentifier());
      endCorrelation.setEndStateIdentifier(Objects.requireNonNull(end.getIdentifier()));

      _publisher.create(endCorrelation);
    }

    return state;
  }

  private void begin (
    final BooleanValueState current,
    @NonNull final LabelState state
  ) {
    @NonNull final LabelState mutation = Duplicator.duplicate(state);
    mutation.setStart(current.getEmissionDate());
    mutation.setEmissionDate(current.getEmissionDate());

    @NonNull final Correlation startCorrelation = Duplicator.duplicate(getStartCorrelation(state));
    startCorrelation.setEndStateIdentifier(current.getIdentifier());

    _publisher.update(mutation);
    _publisher.update(startCorrelation);
  }

  private @NonNull Correlation getStartCorrelation (@NonNull final LabelState label) {
    return _correlations.findFirstCorrelationWithNameAndThatStartBy(
      "start", Objects.requireNonNull(label.getIdentifier())
    ).orElseThrow();
  }

  private @NonNull Optional<Correlation> getEndCorrelation (@NonNull final LabelState label) {
    return _correlations.findFirstCorrelationWithNameAndThatStartBy(
      "end", Objects.requireNonNull(label.getIdentifier())
    );
  }

  private @NonNull Optional<BooleanValueState> getEnd (@NonNull final LabelState label) {
    return getEndCorrelation(label).map(Correlation::getEndStateIdentifier)
             .flatMap(_inputs::find);
  }

  private @NonNull Optional<LabelState> findLabelStateThatStartBy (
    @NonNull final Long stateIdentifier
  ) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().map(Sensor::getIdentifier).orElseThrow(),
      "start",
      stateIdentifier
    ).map(Correlation::getStartStateIdentifier).flatMap(_outputs::find);
  }

  private @NonNull Optional<LabelState> findLabelStateThatEndBy (
    @NonNull final Long stateIdentifier
  ) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().map(Sensor::getIdentifier).orElseThrow(),
      "end",
      stateIdentifier
    ).map(Correlation::getStartStateIdentifier).flatMap(_outputs::find);
  }

  public @NonNull UpDownToLabelSensorConfiguration getConfiguration () {
    return getConfiguration(UpDownToLabelSensorConfiguration.class).orElseThrow();
  }

  public @NonNull Long getOutputSensorIdentifier () {
    return getSensor().map(Sensor::getIdentifier).orElseThrow();
  }

  public @NonNull Long getInputSensorIdentifier () {
    return getConfiguration().getInputSensor();
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return LabelState.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return UpDownToLabelSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:updownlabel";
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof UpDownToLabelSensor) {
      @NonNull final UpDownToLabelSensor otherUpDownToLabelSensor = (UpDownToLabelSensor) other;

      return Objects.equals(
        _publisher,
        otherUpDownToLabelSensor._publisher
      ) && Objects.equals(
        _outputs,
        otherUpDownToLabelSensor._outputs
      ) && Objects.equals(
        _inputs,
        otherUpDownToLabelSensor._inputs
      ) && Objects.equals(
        _correlations,
        otherUpDownToLabelSensor._correlations
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_publisher, _outputs, _inputs, _correlations);
  }
}
