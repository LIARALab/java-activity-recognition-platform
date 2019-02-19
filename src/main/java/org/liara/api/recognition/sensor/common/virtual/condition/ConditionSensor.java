package org.liara.api.recognition.sensor.common.virtual.condition;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.SapaRepositories;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class ConditionSensor
  extends AbstractVirtualSensorHandler
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @NonNull
  private final CorrelationRepository _correlationRepository;

  private final SapaRepositories.@NonNull State _inputStateRepository;

  private final SapaRepositories.@NonNull Boolean _outputStateRepository;


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
      emit(states.get(0), check(states.get(0)));
    }

    for (int index = 1; index < states.size(); ++index) {
      if (check(states.get(index - 1)) != check(states.get(index))) {
        emit(states.get(index), check(states.get(index)));
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
  public void stateWasCreated (final StateEvent.@NonNull WasCreated event) {
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
  private void inputStateWasCreated (
    @NonNull final State stateThatWasCreated
  ) {
    @NonNull
    final Optional<State>          previous =
      _inputStateRepository.findPrevious(stateThatWasCreated);
    @NonNull final Optional<State> next     = _inputStateRepository.findNext(stateThatWasCreated);

    if (previous.isPresent()) {
      if (check(previous.get()) != check(stateThatWasCreated)) {
        if (!next.isPresent()) {
          emit(stateThatWasCreated, check(stateThatWasCreated));
        } else if (check(stateThatWasCreated) != check(next.get())) {
          emit(stateThatWasCreated, check(stateThatWasCreated));
          emit(next.get(), check(next.get()));
        } else {
          moveImageOf(next.get(), stateThatWasCreated, check(stateThatWasCreated));
        }
      }
    } else if (!next.isPresent() || check(stateThatWasCreated) != check(next.get())) {
      emit(stateThatWasCreated, check(stateThatWasCreated));
    } else {
      moveImageOf(next.get(), stateThatWasCreated, check(stateThatWasCreated));
    }
  }

  @Override
  public void stateWillBeMutated (final StateEvent.@NonNull WillBeMutated event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (isInputState(stateThatWillBeMutated)) {
      inputStateWillBeMutated(stateThatWillBeMutated);
    }
  }

  private void inputStateWillBeMutated (@NonNull final State stateThatWillBeMutated) {
    @NonNull
    final Optional<Correlation> correlation = getCorrelationToImage(stateThatWillBeMutated);

    correlation.map(Correlation::getStartStateIdentifier)
      .flatMap(_outputStateRepository::find)
      .ifPresent(this::delete);
  }

  @Override
  public void stateWasMutated (final StateEvent.@NonNull WasMutated event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (isInputState(stateThatWasMutated)) {
      inputStateWasCreated(stateThatWasMutated);
    }
  }

  @Override
  public void stateWillBeDeleted (
    final StateEvent.@NonNull WillBeDeleted event
  ) {
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
  private void inputStateWillBeDeleted (
    @NonNull final State stateThatWillBeDeleted
  ) {
    @NonNull final Optional<State> previous =
      _inputStateRepository.findPrevious(stateThatWillBeDeleted);
    @NonNull final Optional<State> next =
      _inputStateRepository.findNext(stateThatWillBeDeleted);

    if (previous.isPresent()) {
      if (check(previous.get()) != check(stateThatWillBeDeleted)) {
        if (!next.isPresent()) {
          deleteImageOf(stateThatWillBeDeleted);
        } else if (check(stateThatWillBeDeleted) != check(next.get())) {
          deleteImageOf(stateThatWillBeDeleted);
          deleteImageOf(next.get());
        } else {
          moveImageOf(stateThatWillBeDeleted, next.get(), check(stateThatWillBeDeleted));
        }
      }
    } else if (!next.isPresent() || check(stateThatWillBeDeleted) != check(next.get())) {
      deleteImageOf(stateThatWillBeDeleted);
    } else {
      moveImageOf(stateThatWillBeDeleted, next.get(), check(stateThatWillBeDeleted));
    }
  }

  protected abstract boolean check (@NonNull final State state);

  private void deleteImageOf (@NonNull final State preimage) {
    getCorrelationToImage(preimage).map(Correlation::getStartStateIdentifier)
      .flatMap(_outputStateRepository::find)
      .ifPresent(this::delete);
  }

  private void delete (final ValueState.@NonNull Boolean toDelete) {
    _applicationEventPublisher.publishEvent(
      new ApplicationEntityEvent.Delete(this, toDelete)
    );
  }

  private void emit (@NonNull final State state, final boolean up) {
    final ValueState.@NonNull Boolean result = new ValueState.Boolean();

    result.setValue(up);
    result.setEmissionDate(state.getEmissionDate());
    result.setSensorIdentifier(getSensor().map(Sensor::getIdentifier).orElse(null));

    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(result));

    @NonNull final Correlation correlation = new Correlation();

    correlation.setStartStateIdentifier(result.getIdentifier());
    correlation.setEndStateIdentifier(state.getIdentifier());
    correlation.setName("origin");

    _applicationEventPublisher.publishEvent(new ApplicationEntityEvent.Create(correlation));
  }

  private void moveImageOf (
    @NonNull final State from,
    @NonNull final State to,
    final boolean up
  ) {
    @NonNull final Correlation nextCorrelation = Duplicator.duplicate(
      getCorrelationToImage(from).orElseThrow()
    );
    nextCorrelation.setEndStateIdentifier(to.getIdentifier());

    final ValueState.@NonNull Boolean nextImage = Duplicator.duplicate(
      _outputStateRepository.find(
        nextCorrelation.getStartStateIdentifier()
      ).orElseThrow()
    );
    nextImage.setEmissionDate(to.getEmissionDate());
    nextImage.setValue(up);

    _applicationEventPublisher.publishEvent(
      new ApplicationEntityEvent.Update(this, nextCorrelation, nextImage)
    );
  }

  private @NonNull Optional<Correlation> getCorrelationToImage (
    @NonNull final State preimage
  ) {
    return _correlationRepository.findFirstCorrelationWithNameAndThatEndsBy(
      "origin",
      Objects.requireNonNull(preimage.getIdentifier())
    );
  }

  public @NonNull Optional<ConditionSensorConfiguration> getConfiguration () {
    return getConfiguration(ConditionSensorConfiguration.class);
  }

  public @NonNull ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  public @NonNull CorrelationRepository getCorrelationRepository () {
    return _correlationRepository;
  }

  public SapaRepositories.@NonNull State getInputStateRepository () {
    return _inputStateRepository;
  }

  public SapaRepositories.@NonNull Boolean getOutputStateRepository () {
    return _outputStateRepository;
  }
}
