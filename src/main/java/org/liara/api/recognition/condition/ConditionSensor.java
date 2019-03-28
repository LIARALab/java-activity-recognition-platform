package org.liara.api.recognition.condition;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.AnyStateRepository;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillDeleteStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class ConditionSensor
  extends AbstractVirtualSensorHandler
{
  @NonNull
  private final APIEventPublisher _applicationEventPublisher;

  @NonNull
  private final CorrelationRepository _correlationRepository;

  @NonNull
  private final AnyStateRepository _inputStateRepository;

  @NonNull
  private final BooleanValueStateRepository _outputStateRepository;

  public ConditionSensor (@NonNull final ConditionSensorBuilder builder) {
    super();
    _applicationEventPublisher = Objects.requireNonNull(builder.getApplicationEventPublisher());
    _inputStateRepository = Objects.requireNonNull(builder.getInputStateRepository());
    _outputStateRepository = Objects.requireNonNull(builder.getOutputStateRepository());
    _correlationRepository = Objects.requireNonNull(builder.getCorrelationRepository());
  }

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);

    @NonNull final List<State> states = _inputStateRepository.find(
      getConfiguration().map(ConditionSensorConfiguration::getInputSensor).orElseThrow(),
      Cursor.ALL
    );

    if (states.size() > 0) {
      emit(states.get(0));
    }

    for (int index = 1; index < states.size(); ++index) {
      if (check(states.get(index - 1)) != check(states.get(index))) {
        emit(states.get(index));
      }
    }
  }

  private boolean isInputState (@NonNull final State state) {
    return Objects.equals(
      state.getSensorIdentifier(),
      getConfiguration().map(ConditionSensorConfiguration::getInputSensor).orElse(null)
    );
  }

  @Override
  public void stateWasCreated (@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (isInputState(stateThatWasCreated)) {
      inputStateWasCreated(stateThatWasCreated);
    }
  }

  /**
   * <table>
   * <tr>
   * <th>Previous</th>
   * <th>Current</th>
   * <th>Next</th>
   * <th>Result</th>
   * </tr>
   * <tr><td>VRAI</td><td>VRAI</td><td>VRAI</td><!-- = --><td>NOTHING </td></tr>
   * <tr><td>VRAI</td><td>VRAI</td><td>NULL</td><!-- = --><td>NOTHING </td></tr>
   * <tr><td>FAUX</td><td>FAUX</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>FAUX</td><td>FAUX</td><td>NULL</td><!-- = --><td>NOTHING </td></tr>
   * <tr><td>FAUX</td><td>FAUX</td><td>VRAI</td><!-- = --><td>NOTHING </td></tr>
   * <tr><td>VRAI</td><td>VRAI</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>NULL</td><td>FAUX</td><td>VRAI</td><!-- = --><td>EMIT CURRENT </td></tr>
   * <tr><td>NULL</td><td>FAUX</td><td>NULL</td><!-- = --><td>EMIT CURRENT </td></tr>
   * <tr><td>NULL</td><td>VRAI</td><td>FAUX</td><!-- = --><td>EMIT CURRENT </td></tr>
   * <tr><td>NULL</td><td>VRAI</td><td>NULL</td><!-- = --><td>EMIT CURRENT </td></tr>
   * <tr><td>NULL</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE NEXT TO CURRENT </td></tr>
   * <tr><td>NULL</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE NEXT TO CURRENT</td></tr>
   * <tr><td>VRAI</td><td>FAUX</td><td>VRAI</td><!-- = --><td>EMIT CURRENT, EMIT NEXT </td></tr>
   * <tr><td>FAUX</td><td>VRAI</td><td>FAUX</td><!-- = --><td>EMIT CURRENT, EMIT NEXT </td></tr>
   * <tr><td>VRAI</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE NEXT TO CURRENT </td></tr>
   * <tr><td>FAUX</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE NEXT TO CURRENT </td></tr>
   * <tr><td>VRAI</td><td>FAUX</td><td>NULL</td><!-- = --><td>EMIT CURRENT </td></tr>
   * <tr><td>FAUX</td><td>VRAI</td><td>NULL</td><!-- = --><td>EMIT CURRENT</td></tr>
   * </table>
   *
   * @param stateThatWasCreated The input state that was created.
   */
  private void inputStateWasCreated (@NonNull final State stateThatWasCreated) {
    @NonNull
    final Optional<State>          previous = _inputStateRepository.findPrevious(stateThatWasCreated);
    @NonNull final Optional<State> next     = _inputStateRepository.findNext(stateThatWasCreated);

    if (!previous.isPresent()) {
      if (!next.isPresent() || check(stateThatWasCreated) != check(next.get())) {
        emit(stateThatWasCreated);
      } else {
        move(next.get(), stateThatWasCreated);
      }
    } else if (check(previous.get()) != check(stateThatWasCreated)) {
      if (!next.isPresent()) {
        emit(stateThatWasCreated);
      } else if (check(stateThatWasCreated) != check(next.get())) {
        emit(stateThatWasCreated);
        emit(next.get());
      } else {
        move(next.get(), stateThatWasCreated);
      }
    }
  }

  @Override
  public void stateWillBeMutated (@NonNull final WillUpdateStateEvent event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (isInputState(stateThatWillBeMutated)) {
      inputStateWillBeMutated(stateThatWillBeMutated);
    }
  }

  private void inputStateWillBeMutated (@NonNull final State stateThatWillBeMutated) {
    @NonNull final Optional<Correlation> correlation = getCorrelationTo(stateThatWillBeMutated);

    correlation.map(Correlation::getStartStateIdentifier)
      .flatMap(_outputStateRepository::find)
      .ifPresent(this::delete);
  }

  @Override
  public void stateWasMutated (@NonNull final DidUpdateStateEvent event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (isInputState(stateThatWasMutated)) {
      inputStateWasCreated(stateThatWasMutated);
    }
  }

  @Override
  public void stateWillBeDeleted (@NonNull final WillDeleteStateEvent event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (isInputState(stateThatWillBeDeleted)) {
      inputStateWillBeDeleted(stateThatWillBeDeleted);
    }
  }

  /**
   * <table>
   * <tr>
   * <th>Previous</th>
   * <th>Current</th>
   * <th>Next</th>
   * <th>Result</th>
   * </tr>
   * <tr><td>VRAI</td><td>VRAI</td><td>VRAI</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>VRAI</td><td>VRAI</td><td>NULL</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>FAUX</td><td>FAUX</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>FAUX</td><td>FAUX</td><td>NULL</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>FAUX</td><td>FAUX</td><td>VRAI</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>VRAI</td><td>VRAI</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   * <tr><td>NULL</td><td>FAUX</td><td>VRAI</td><!-- = --><td>DELETE CURRENT</td></tr>
   * <tr><td>NULL</td><td>FAUX</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   * <tr><td>NULL</td><td>VRAI</td><td>FAUX</td><!-- = --><td>DELETE CURRENT</td></tr>
   * <tr><td>NULL</td><td>VRAI</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   * <tr><td>NULL</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   * <tr><td>NULL</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   * <tr><td>VRAI</td><td>FAUX</td><td>VRAI</td><!-- = --><td>DELETE CURRENT, DELETE NEXT</td></tr>
   * <tr><td>FAUX</td><td>VRAI</td><td>FAUX</td><!-- = --><td>DELETE CURRENT, DELETE NEXT</td></tr>
   * <tr><td>VRAI</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   * <tr><td>FAUX</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   * <tr><td>VRAI</td><td>FAUX</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   * <tr><td>FAUX</td><td>VRAI</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   * </table>
   *
   * @param stateThatWillBeDeleted Input state that will be deleted.
   */
  private void inputStateWillBeDeleted (@NonNull final State stateThatWillBeDeleted) {
    @NonNull final Optional<State> previous = _inputStateRepository.findPrevious(
      stateThatWillBeDeleted);
    @NonNull
    final Optional<State>          next     = _inputStateRepository.findNext(stateThatWillBeDeleted);

    if (previous.isPresent()) {
      if (check(previous.get()) != check(stateThatWillBeDeleted)) {
        if (!next.isPresent()) {
          deleteImageOf(stateThatWillBeDeleted);
        } else if (check(stateThatWillBeDeleted) != check(next.get())) {
          deleteImageOf(stateThatWillBeDeleted);
          deleteImageOf(next.get());
        } else {
          move(stateThatWillBeDeleted, next.get());
        }
      }
    } else if (!next.isPresent() || check(stateThatWillBeDeleted) != check(next.get())) {
      deleteImageOf(stateThatWillBeDeleted);
    } else {
      move(stateThatWillBeDeleted, next.get());
    }
  }

  protected abstract boolean check (@NonNull final State state);

  private void deleteImageOf (@NonNull final State preimage) {
    getCorrelationTo(preimage).map(Correlation::getStartStateIdentifier)
      .flatMap(_outputStateRepository::find)
      .ifPresent(this::delete);
  }

  private void delete (@NonNull final BooleanValueState toDelete) {
    _applicationEventPublisher.delete(toDelete);
  }

  private void emit (@NonNull final State state) {
    Logger.getLogger(getClass().getName()).info(
      "[" + getSensor().orElseThrow().getIdentifier() + "] Emit " + state.getEmissionDate() +
      " (" + check(state) + ")"
    );

    @NonNull final BooleanValueState result = new BooleanValueState();

    result.setValue(check(state));
    result.setEmissionDate(state.getEmissionDate());
    result.setSensorIdentifier(getSensor().map(Sensor::getIdentifier).orElseThrow());

    _applicationEventPublisher.create(result);

    @NonNull final Correlation correlation = new Correlation();

    correlation.setStartStateIdentifier(result.getIdentifier());
    correlation.setEndStateIdentifier(state.getIdentifier());
    correlation.setName("origin");

    _applicationEventPublisher.create(correlation);
  }

  private void move (@NonNull final State from, @NonNull final State to) {
    Logger.getLogger(getClass().getName()).info(
      "[" + getSensor().orElseThrow().getIdentifier() + "] Moving " + from.getEmissionDate() +
      " to " + to.getEmissionDate() + " (" + check(to) + ")"
    );

    @NonNull final Correlation correlation = getCorrelationTo(from).map(Duplicator::duplicate)
                                               .orElseThrow();
    @NonNull final BooleanValueState image = Duplicator.duplicate(
      _outputStateRepository.getAt(Objects.requireNonNull(correlation.getStartStateIdentifier()))
    );

    correlation.setEndStateIdentifier(to.getIdentifier());
    image.setEmissionDate(to.getEmissionDate());

    _applicationEventPublisher.update(image, correlation);
  }

  private @NonNull Optional<Correlation> getCorrelationTo (@NonNull final State preimage) {
    return _correlationRepository.findFirstCorrelationWithNameAndThatEndsBy(
      "origin", Objects.requireNonNull(preimage.getIdentifier())
    );
  }

  public @NonNull Optional<ConditionSensorConfiguration> getConfiguration () {
    return getConfiguration(ConditionSensorConfiguration.class);
  }

  public @NonNull APIEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  public @NonNull CorrelationRepository getCorrelationRepository () {
    return _correlationRepository;
  }

  public @NonNull AnyStateRepository getInputStateRepository () {
    return _inputStateRepository;
  }

  public @NonNull BooleanValueStateRepository getOutputStateRepository () {
    return _outputStateRepository;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ConditionSensor) {
      @NonNull final ConditionSensor otherConditionSensor = (ConditionSensor) other;

      return Objects.equals(
        _applicationEventPublisher,
        otherConditionSensor.getApplicationEventPublisher()
      ) && Objects.equals(
        _correlationRepository,
        otherConditionSensor.getCorrelationRepository()
      ) && Objects.equals(
        _inputStateRepository,
        otherConditionSensor.getInputStateRepository()
      ) && Objects.equals(
        _outputStateRepository,
        otherConditionSensor.getOutputStateRepository()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _applicationEventPublisher,
      _correlationRepository,
      _inputStateRepository,
      _outputStateRepository
    );
  }
}
