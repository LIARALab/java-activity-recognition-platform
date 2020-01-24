package org.liara.api.recognition.sensor.agglomerate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillDeleteStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.api.validation.RestOperationType;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AgglomerationSensor
        extends AbstractVirtualSensorHandler
        implements ComputedSensorType {
  @NonNull
  private final APIEventPublisher _events;

  @NonNull
  private final BooleanValueStateRepository _booleans;

  @NonNull
  private final CorrelationRepository _correlations;

  public AgglomerationSensor(@NonNull final AgglomerationSensorBuilder builder) {
    _events = Objects.requireNonNull(builder.getEventPublisher());
    _booleans = Objects.requireNonNull(builder.getBooleanStateRepository());
    _correlations = Objects.requireNonNull(builder.getCorrelationRepository());
  }

  @Override
  public void initialize(@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);

    @NonNull final Iterator<@NonNull BooleanValueState> states = (
            _booleans.find(getConfiguration().getSource(), Cursor.ALL).iterator()
    );

    if (!states.hasNext()) return;

    @NonNull BooleanValueState lastChange;

    do {
      lastChange = states.next();
    } while (!lastChange.requireValue() && states.hasNext());

    if (lastChange.requireValue()) emit(lastChange);

    while (states.hasNext()) {
      @NonNull final BooleanValueState state = states.next();

      if (lastChange.requireValue() != state.requireValue()) {
        if (state.requireValue() && !areAgglomerated(lastChange, state)) {
          emit(lastChange);
          emit(state);
        }

        lastChange = state;
      }
    }

    if (!lastChange.requireValue()) emit(lastChange);
  }

  @Override
  public void stateWasCreated(@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (match(stateThatWasCreated)) {
      sourceStateWasCreated((BooleanValueState) stateThatWasCreated);
    }
  }

  /**
   * NONE  FALSE FALSE - NOTHING
   * NONE  FALSE TRUE  - NOTHING
   * NONE  FALSE NONE  - NOTHING
   * FALSE FALSE FALSE - NOTHING
   * FALSE FALSE TRUE  - NOTHING
   * FALSE FALSE NONE  - NOTHING
   * TRUE  TRUE  FALSE - NOTHING
   * TRUE  TRUE  TRUE  - NOTHING
   * TRUE  TRUE  NONE  - NOTHING
   * <p>
   * A A X - NOTHING
   *
   * @param stateThatWasCreated
   */
  private void sourceStateWasCreated(@NonNull final BooleanValueState stateThatWasCreated) {
    @NonNull final Optional<BooleanValueState> previous = findPrevious(stateThatWasCreated);

    if (previous.isPresent()) {
      if (previous.get().requireValue() != stateThatWasCreated.requireValue()) {
        variantSourceStateWasCreated(previous.get(), stateThatWasCreated);
      }
    } else if (stateThatWasCreated.requireValue()) {
      variantSourceStateWasCreated(null, stateThatWasCreated);
    }
  }

  /**
   * FALSE TRUE FALSE  - CREATE OR AGGLOMERATE
   * FALSE TRUE NONE   - CREATE OR AGGLOMERATE
   * NONE  TRUE  FALSE - CREATE OR AGGLOMERATE
   * NONE  TRUE  NONE  - CREATE OR AGGLOMERATE
   * FALSE TRUE TRUE   - EXPAND OR AGGLOMERATE
   * NONE  TRUE  TRUE  - EXPAND OR AGGLOMERATE
   * TRUE  FALSE FALSE - REDUCE OR SPLIT
   * TRUE  FALSE TRUE  - SPLIT OR AGGLOMERATE
   * TRUE  FALSE NONE  - CREATE
   *
   * @param stateThatWasCreated
   */
  private void variantSourceStateWasCreated(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated
  ) {
    @NonNull final Optional<BooleanValueState> next = findNextChange(stateThatWasCreated);

    if (stateThatWasCreated.requireValue()) {
      if (next.isPresent() && next.get().requireValue()) {
        expandOrAgglomerate(previous, stateThatWasCreated, next.orElse(null));
      } else {
        createOrAgglomerate(previous, stateThatWasCreated, next.orElse(null));
      }
    } else if (!next.isPresent()) {
      emit(stateThatWasCreated);
    } else if (next.get().requireValue()) {
      splitOrAgglomerate(previous, stateThatWasCreated, next.get());
    } else {
      reduceOrSplit(previous, stateThatWasCreated, next.get());
    }
  }

  /**
   * TRUE FALSE TRUE - SPLIT OR AGGLOMERATE
   *
   * @param previous
   * @param stateThatWasCreated
   * @param next
   */
  private void splitOrAgglomerate(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState next
  ) {
    if (!areAgglomerated(stateThatWasCreated, next)) {
      emit(stateThatWasCreated);
      emit(next);
    }
  }

  /**
   * TRUE FALSE FALSE - REDUCE OR SPLIT
   *
   * @param previous
   * @param stateThatWasCreated
   * @param next
   */
  private void reduceOrSplit(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState next
  ) {
    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWasCreated.requireEmissionDate(),
            stateThatWasCreated.requireSensorIdentifier(),
            true
    );

    if (nextTrue.isPresent()) {
      if (areAgglomerated(stateThatWasCreated, nextTrue.get())) {
        return;
      } else if (areAgglomerated(next, nextTrue.get())) {
        emit(stateThatWasCreated);
        emit(next);
      } else {
        move(next, stateThatWasCreated);
      }
    } else {
      move(next, stateThatWasCreated);
    }
  }

  /**
   * FALSE TRUE TRUE - EXPAND OR AGGLOMERATE
   * NONE  TRUE TRUE - EXPAND OR AGGLOMERATE
   *
   * @param previous
   * @param stateThatWasCreated
   * @param nextState
   */
  private void expandOrAgglomerate(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState nextState
  ) {
    if (previous == null) {
      move(nextState, stateThatWasCreated);
    } else {
      @NonNull final Optional<BooleanValueState> previousTrue = _booleans.findPreviousWithValue(
              stateThatWasCreated.requireEmissionDate(),
              stateThatWasCreated.requireSensorIdentifier(),
              true
      );

      if (previousTrue.isPresent()) {
        @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
                previousTrue.get().requireEmissionDate(),
                stateThatWasCreated.requireSensorIdentifier(),
                false
        ).orElseThrow();

        if (!areAgglomerated(previousFalse, stateThatWasCreated)) {
          move(nextState, stateThatWasCreated);
        } else if (!areAgglomerated(previousFalse, stateThatWasCreated)) {
          delete(nextState);
          delete(previousFalse);
        }
      } else {
        move(nextState, stateThatWasCreated);
      }
    }
  }

  /**
   * FALSE TRUE FALSE - CREATE OR AGGLOMERATE
   * FALSE TRUE NONE  - CREATE OR AGGLOMERATE
   * NONE  TRUE FALSE - CREATE OR AGGLOMERATE
   * NONE  TRUE NONE  - CREATE OR AGGLOMERATE
   *
   * @param previous
   * @param stateThatWasCreated
   * @param nextState
   */
  private void createOrAgglomerate(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @Nullable final BooleanValueState nextState
  ) {
    if (previous == null && nextState == null) {
      emit(stateThatWasCreated);
    } else if (previous == null) {
      createOrAgglomerateNext(stateThatWasCreated, nextState);
    } else if (nextState == null) {
      createOrAgglomeratePrevious(previous, stateThatWasCreated);
    } else {
      createOrAgglomerateInner(previous, stateThatWasCreated, nextState);
    }
  }

  /**
   * FALSE TRUE FALSE - CREATE OR AGGLOMERATE
   *
   * @param previous
   * @param stateThatWasCreated
   * @param nextState
   */
  private void createOrAgglomerateInner(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState nextState
  ) {
    @NonNull final Optional<BooleanValueState> previousTrue = _booleans.findPreviousWithValue(
            stateThatWasCreated.requireEmissionDate(),
            stateThatWasCreated.requireSensorIdentifier(),
            true
    );

    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWasCreated.requireEmissionDate(),
            stateThatWasCreated.requireSensorIdentifier(),
            true
    );

    if (previousTrue.isPresent() && nextTrue.isPresent()) {
      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWasCreated.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (!areAgglomerated(previousFalse, nextTrue.get())) {
        if (areAgglomerated(previousFalse, stateThatWasCreated)) {
          delete(previousFalse);
        } else {
          emit(stateThatWasCreated);
        }

        if (areAgglomerated(nextState, nextTrue.get())) {
          delete(nextTrue.get());
        } else {
          emit(nextState);
        }
      } // always agglomerated
    } else if (previousTrue.isPresent()) {
      emit(nextState);

      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWasCreated.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (areAgglomerated(previousFalse, stateThatWasCreated)) {
        delete(previousFalse);
      } else {
        emit(stateThatWasCreated);
      }
    } else if (nextTrue.isPresent()) {
      emit(stateThatWasCreated);

      if (areAgglomerated(nextState, nextTrue.get())) {
        delete(nextTrue.get());
      } else {
        emit(nextState);
      }
    } else {
      emit(stateThatWasCreated);
      emit(nextState);
    }
  }

  /**
   * FALSE TRUE NONE  - CREATE OR AGGLOMERATE
   *
   * @param previous
   * @param stateThatWasCreated
   */
  private void createOrAgglomeratePrevious(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated
  ) {
    @NonNull final Optional<BooleanValueState> previousTrue = _booleans.findPreviousWithValue(
            stateThatWasCreated.requireEmissionDate(),
            stateThatWasCreated.requireSensorIdentifier(),
            true
    );

    if (previousTrue.isPresent()) {
      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWasCreated.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (areAgglomerated(previousFalse, stateThatWasCreated)) {
        delete(previousFalse);
      } else {
        emit(stateThatWasCreated);
      }
    } else {
      emit(stateThatWasCreated);
    }
  }

  /**
   * NONE  TRUE FALSE - CREATE OR AGGLOMERATE
   *
   * @param stateThatWasCreated
   * @param nextState
   */
  private void createOrAgglomerateNext(
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState nextState
  ) {
    emit(stateThatWasCreated);

    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWasCreated.requireEmissionDate(),
            stateThatWasCreated.requireSensorIdentifier(),
            true
    );

    if (nextTrue.isPresent() && areAgglomerated(nextState, nextTrue.get())) {
      delete(nextTrue.get());
    } else {
      emit(nextState);
    }
  }

  @Override
  public void stateWillBeMutated(@NonNull final WillUpdateStateEvent event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (match(stateThatWillBeMutated)) {
      sourceStateWillBeDeleted((BooleanValueState) stateThatWillBeMutated);
    }
  }

  @Override
  public void stateWasMutated(@NonNull final DidUpdateStateEvent event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (match(stateThatWasMutated)) {
      sourceStateWasCreated((BooleanValueState) stateThatWasMutated);
    }
  }

  @Override
  public void stateWillBeDeleted(@NonNull final WillDeleteStateEvent event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (match(stateThatWillBeDeleted)) {
      sourceStateWillBeDeleted((BooleanValueState) stateThatWillBeDeleted);
    }
  }


  /**
   * NONE  FALSE FALSE - NOTHING
   * NONE  FALSE TRUE  - NOTHING
   * NONE  FALSE NONE  - NOTHING
   * FALSE FALSE FALSE - NOTHING
   * FALSE FALSE TRUE  - NOTHING
   * FALSE FALSE NONE  - NOTHING
   * TRUE  TRUE  FALSE - NOTHING
   * TRUE  TRUE  TRUE  - NOTHING
   * TRUE  TRUE  NONE  - NOTHING
   *
   * @param stateThatWillBeDeleted
   */
  private void sourceStateWillBeDeleted(@NonNull final BooleanValueState stateThatWillBeDeleted) {
    @NonNull final Optional<BooleanValueState> previous = findPrevious(stateThatWillBeDeleted);

    if (previous.isPresent()) {
      if (previous.get().requireValue() != stateThatWillBeDeleted.requireValue()) {
        variantSourceStateWillBeDeleted(stateThatWillBeDeleted);
      }
    } else if (stateThatWillBeDeleted.requireValue()) {
      variantSourceStateWillBeDeleted(stateThatWillBeDeleted);
    }
  }

  /**
   * FALSE TRUE FALSE - DROP OR SPLIT
   * FALSE TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE FALSE - DROP OR SPLIT
   * <p>
   * FALSE TRUE TRUE - REDUCE LEFT OR SPLIT
   * NONE  TRUE TRUE - REDUCE LEFT OR SPLIT
   * <p>
   * TRUE FALSE FALSE - EXPAND RIGHT OR AGGREGATE
   * TRUE FALSE TRUE  - MAY MERGE
   * TRUE FALSE NONE  - DROP
   *
   * @param stateThatWillBeDeleted
   */
  private void variantSourceStateWillBeDeleted(@NonNull final BooleanValueState stateThatWillBeDeleted) {
    @NonNull final Optional<BooleanValueState> next = findNextChange(stateThatWillBeDeleted);

    if (stateThatWillBeDeleted.requireValue()) {
      if (next.isPresent() && next.get().requireValue()) {
        reduceLeftOrSplit(stateThatWillBeDeleted, next.orElse(null));
      } else {
        dropOrSplit(stateThatWillBeDeleted, next.orElse(null));
      }
    } else if (!next.isPresent()) {
      delete(stateThatWillBeDeleted);
    } else if (next.get().requireValue()) {
      mayMerge(stateThatWillBeDeleted, next.get());
    } else {
      expandRightOrAggregate(stateThatWillBeDeleted, next.get());
    }
  }

  /**
   * TRUE FALSE FALSE - EXPAND OR AGGREGATE
   *
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void expandRightOrAggregate(
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
    @NonNull final Optional<BooleanValueState> nextChange = findNextChange(stateThatWillBeDeleted);

    if (nextChange.isPresent() && nextChange.get().requireValue()) {
      if (areAgglomerated(next, nextChange.get())) {
        delete(stateThatWillBeDeleted);
        delete(nextChange.get());
      } else {
        move(stateThatWillBeDeleted, next);
      }
    }
  }

  /**
   * TRUE FALSE TRUE - MERGE
   *
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void mayMerge(
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @Nullable final BooleanValueState next
  ) {
    @NonNull final Optional<BooleanValueState> nextChange = findNextChange(stateThatWillBeDeleted);

    if (nextChange.isPresent() && nextChange.get().requireValue()) {
      delete(stateThatWillBeDeleted);
      delete(nextChange.get());
    }
  }

  /**
   * FALSE TRUE FALSE - DROP OR SPLIT
   * FALSE TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE FALSE - DROP OR SPLIT
   *
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void dropOrSplit(
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @Nullable final BooleanValueState next
  ) {
    @NonNull final Optional<BooleanValueState> previousChange = findPreviousChange(stateThatWillBeDeleted);
    @NonNull final Optional<BooleanValueState> nextTrue = findNextSource(stateThatWillBeDeleted, true);
    @NonNull final Optional<BooleanValueState> nextChange = findNextChange(stateThatWillBeDeleted);

    if (previousChange.isPresent() && previousChange.get().requireValue()) {
      @NonNull final Optional<BooleanValueState> previousTrue = findPreviousSource(stateThatWillBeDeleted, true);
      @NonNull final Optional<BooleanValueState> previousFalse = findNextSource(previousTrue.orElseThrow(), false);

      if (nextTrue.isPresent()) {
        Objects.requireNonNull(next);

        if (!areAgglomerated(next, nextTrue.get())) {
          emit(previousFalse.orElseThrow());
          delete(next);
        } else if (!areAgglomerated(previousFalse.orElseThrow(), nextTrue.get())) {
          emit(previousFalse.get());
          emit(nextTrue.get());
        }
      } else {
        nextChange.ifPresent(this::delete);
        emit(previousFalse.orElseThrow());
      }
    } else {
      delete(stateThatWillBeDeleted);

      if (nextTrue.isPresent()) {
        if (
                nextChange.isPresent() &&
                        nextChange.get().requireEmissionDate().isBefore(nextTrue.get().requireEmissionDate())
        ) {
          delete(nextChange.get());
        } else {
          emit(nextTrue.get());
        }
      } else {
        nextChange.ifPresent(this::delete);
      }
    }
  }

  /**
   * FALSE TRUE TRUE - REDUCE OR SPLIT
   * NONE  TRUE TRUE - REDUCE OR SPLIT
   *
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void reduceLeftOrSplit(
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
    @NonNull final Optional<BooleanValueState> previousChange = findPreviousChange(stateThatWillBeDeleted);

    if (previousChange.isPresent() && previousChange.get().requireValue()) {
      @NonNull final Optional<BooleanValueState> previousFalse = findNextSource(previousChange.get(), false);

      if (!areAgglomerated(previousFalse.orElseThrow(), next)) {
        emit(previousFalse.get());
        emit(next);
      }
    } else {
      move(stateThatWillBeDeleted, next);
    }
  }

  private boolean areAgglomerated(@NonNull final State first, @NonNull final State second) {
    @NonNull final ZonedDateTime start;
    @NonNull final ZonedDateTime end;

    if (first.requireEmissionDate().isBefore(second.requireEmissionDate())) {
      start = first.requireEmissionDate();
      end = second.requireEmissionDate();
    } else {
      start = second.requireEmissionDate();
      end = first.requireEmissionDate();
    }

    return end.toEpochSecond() - start.toEpochSecond() <= getConfiguration().getDuration();
  }

  public boolean match(@NonNull final State state) {
    return Objects.equals(state.getSensorIdentifier(), getConfiguration().getSource());
  }

  private @NonNull Optional<BooleanValueState> findPrevious(@NonNull final State state) {
    return _booleans.findPrevious(state.requireEmissionDate(), getConfiguration().getSource());
  }

  private @NonNull Optional<BooleanValueState> findNext(@NonNull final State state) {
    return _booleans.findNext(state.requireEmissionDate(), getConfiguration().getSource());
  }

  private @NonNull Optional<BooleanValueState> findNextSource(@NonNull final State state, final boolean value) {
    return _booleans.findNextWithValue(state.requireEmissionDate(), getConfiguration().getSource(), value);
  }

  private @NonNull Optional<BooleanValueState> findPreviousSource(@NonNull final State state, final boolean value) {
    return _booleans.findPreviousWithValue(state.requireEmissionDate(), getConfiguration().getSource(), value);
  }

  private @NonNull Optional<BooleanValueState> findPreviousChange(@NonNull final State state) {
    return _booleans.findPrevious(state.requireEmissionDate(), getSensor().orElseThrow().requireIdentifier());
  }

  private @NonNull Optional<BooleanValueState> findNextChange(@NonNull final State state) {
    return _booleans.findNext(state.requireEmissionDate(), getSensor().orElseThrow().requireIdentifier());
  }

  private @NonNull Optional<BooleanValueState> getOutputFromInput(@NonNull final State output) {
    return findCorrelationOfOutput(output)
            .map(Correlation::getEndStateIdentifier)
            .flatMap(_booleans::find);
  }

  private void delete(@NonNull final BooleanValueState state) {
    if (state.requireSensorIdentifier().equals(getConfiguration().getSource())) {
      delete(getOutputFromInput(state).orElseThrow());
    } else {
      _events.delete(state);
    }
  }

  private void move(@NonNull final BooleanValueState from, @NonNull final BooleanValueState to) {
    delete(from);
    emit(to);
  }

  private void emit(@NonNull final BooleanValueState input) {
    @NonNull final BooleanValueState result = new BooleanValueState();

    result.setValue(input.getValue());
    result.setEmissionDate(input.getEmissionDate());
    result.setSensorIdentifier(getSensor().orElseThrow().getIdentifier());

    _events.create(result);

    @NonNull final Correlation correlation = new Correlation();

    correlation.setStartStateIdentifier(result.getIdentifier());
    correlation.setEndStateIdentifier(input.getIdentifier());
    correlation.setName("origin");

    _events.create(correlation);
  }

  private @NonNull Optional<Correlation> findCorrelationOfInput(@NonNull final State input) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
            getSensor().orElseThrow().requireIdentifier(), "origin", input.requireIdentifier()
    );
  }

  private @NonNull Optional<Correlation> findCorrelationOfOutput(@NonNull final State output) {
    return _correlations.findFirstCorrelationWithNameAndThatStartBy("origin", output.requireIdentifier());
  }

  public @NonNull AgglomerationSensorConfiguration getConfiguration() {
    return getConfiguration(AgglomerationSensorConfiguration.class).orElseThrow();
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass() {
    return BooleanValueState.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass() {
    return AgglomerationSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName() {
    return "liara:agglomeration";
  }
}
