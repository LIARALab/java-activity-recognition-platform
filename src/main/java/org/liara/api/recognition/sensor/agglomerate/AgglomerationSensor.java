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
import org.liara.api.logging.Loggable;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
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
        implements ComputedSensorType, Loggable {
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

    if (states.hasNext()) {
      @NonNull BooleanValueState lastChange;

      do {
        lastChange = states.next();
      } while (!lastChange.requireValue() && states.hasNext());

      if (lastChange.requireValue()) {
        emit(lastChange);

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

        if (!lastChange.requireValue()) {
          emit(lastChange);
        }
      }
    }
  }

  private @NonNull String toString (@Nullable final BooleanValueState state) {
    if (state == null) {
      return "null";
    } else {
      return state.getEmissionDate() + " " + state.getValue();
    }
  }

  private @NonNull String toString (@Nullable final DidCreateStateEvent event) {
    if (event == null) {
      return "null";
    } else {
      return event.getState().getEmissionDate() + " " + event.getState().getSensorIdentifier() + " !!" +
              event.getState().getClass().getName();
    }
  }

  @Override
  public void stateWasCreated(@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

//    info("stateWasCreated(" + toString(event) + ")");
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
    @NonNull final Optional<BooleanValueState> previous = _booleans.findPrevious(stateThatWasCreated);

//    info("sourceStateWasCreated(" + toString(stateThatWasCreated) + ")");

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
//    info("variantSourceStateWasCreated(" + toString(previous) + ", " + toString(stateThatWasCreated) + ")");
    @NonNull final Optional<BooleanValueState> next = _booleans.findNext(stateThatWasCreated);

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
//    info("splitOrAgglomerate(" + toString(previous) + ", " + toString(stateThatWasCreated) + ", " + toString(next) + ")");
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
//    info("reduceOrSplit(" + toString(previous) + ", " + toString(stateThatWasCreated) + ", " + toString(next) + ")");
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
        emit(nextTrue.get());
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
   * @param next
   */
  private void expandOrAgglomerate(
    @Nullable final BooleanValueState previous,
    @NonNull final BooleanValueState stateThatWasCreated,
    @NonNull final BooleanValueState next
  ) {
//    info("expandOrAgglomerate(" + toString(previous) + ", " + toString(stateThatWasCreated) + ", " + toString(next) + ")");
    if (previous == null) {
      move(next, stateThatWasCreated);
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
          move(next, stateThatWasCreated);
        } else if (!areAgglomerated(previousFalse, next)) {
          delete(next);
          delete(previousFalse);
        }
      } else {
        move(next, stateThatWasCreated);
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
   * @param next
   */
  private void createOrAgglomerate(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @Nullable final BooleanValueState next
  ) {
//    info("createOrAgglomerate(" + toString(previous) + ", " + toString(stateThatWasCreated) + ", " + toString(next) + ")");
    if (previous == null && next == null) {
      emit(stateThatWasCreated);
    } else if (previous == null) {
      createOrAgglomerateNext(stateThatWasCreated, next);
    } else if (next == null) {
      createOrAgglomeratePrevious(previous, stateThatWasCreated);
    } else {
      createOrAgglomerateInner(previous, stateThatWasCreated, next);
    }
  }

  /**
   * FALSE TRUE FALSE - CREATE OR AGGLOMERATE
   *
   * @param previous
   * @param stateThatWasCreated
   * @param next
   */
  private void createOrAgglomerateInner(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState next
  ) {
//    info("createOrAgglomerateInner(" + toString(previous) + ", " + toString(stateThatWasCreated) + ", " + toString(next) + ")");
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

        if (areAgglomerated(next, nextTrue.get())) {
          delete(nextTrue.get());
        } else {
          emit(next);
        }
      } // always agglomerated
    } else if (previousTrue.isPresent()) {
      emit(next);

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

      if (areAgglomerated(next, nextTrue.get())) {
        delete(nextTrue.get());
      } else {
        emit(next);
      }
    } else {
      emit(stateThatWasCreated);
      emit(next);
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
//    info("createOrAgglomeratePrevious(" + toString(previous) + ", " + toString(stateThatWasCreated) + ")");
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
   * @param next
   */
  private void createOrAgglomerateNext(
          @NonNull final BooleanValueState stateThatWasCreated,
          @NonNull final BooleanValueState next
  ) {
//    info("createOrAgglomerateNext(" + toString(stateThatWasCreated) + ", " + toString(next) + ")");
    emit(stateThatWasCreated);

    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWasCreated.requireEmissionDate(),
            stateThatWasCreated.requireSensorIdentifier(),
            true
    );

    if (nextTrue.isPresent() && areAgglomerated(next, nextTrue.get())) {
      delete(nextTrue.get());
    } else {
      emit(next);
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
  private void sourceStateWillBeDeleted (@NonNull final BooleanValueState stateThatWillBeDeleted) {
//    info("sourceStateWillBeDeleted(" + toString(stateThatWillBeDeleted) + ")");
    @NonNull final Optional<BooleanValueState> previous = _booleans.findPrevious(stateThatWillBeDeleted);

    if (previous.isPresent()) {
      if (previous.get().requireValue() != stateThatWillBeDeleted.requireValue()) {
        variantSourceStateWillBeDeleted(previous.orElse(null), stateThatWillBeDeleted);
      }
    } else if (stateThatWillBeDeleted.requireValue()) {
      variantSourceStateWillBeDeleted(previous.orElse(null), stateThatWillBeDeleted);
    }
  }

  /**
   * FALSE TRUE FALSE - DROP OR SPLIT
   * FALSE TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE FALSE - DROP OR SPLIT
   * FALSE TRUE TRUE - REDUCE LEFT OR SPLIT
   * NONE  TRUE TRUE - REDUCE LEFT OR SPLIT
   * TRUE FALSE FALSE - EXPAND RIGHT OR AGGREGATE
   * TRUE FALSE TRUE  - MERGE
   * TRUE FALSE NONE  - DROP
   *
   * @param previous
   * @param stateThatWillBeDeleted
   */
  private void variantSourceStateWillBeDeleted(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted
  ) {
//    info("variantSourceStateWillBeDeleted(" + toString(previous) + ", " + toString(stateThatWillBeDeleted) + ")");
    @NonNull final Optional<BooleanValueState> next = _booleans.findNext(stateThatWillBeDeleted);

    if (stateThatWillBeDeleted.requireValue()) {
      if (next.isPresent() && next.get().requireValue()) {
        reduceLeftOrSplit(previous, stateThatWillBeDeleted, next.orElse(null));
      } else {
        dropOrSplit(previous, stateThatWillBeDeleted, next.orElse(null));
      }
    } else if (!next.isPresent()) {
      delete(stateThatWillBeDeleted);
    } else if (next.get().requireValue()) {
      merge(previous, stateThatWillBeDeleted, next.get());
    } else {
      expandRightOrAggregate(previous, stateThatWillBeDeleted, next.get());
    }
  }

  /**
   * TRUE FALSE FALSE - EXPAND OR AGGREGATE
   *
   * @param previous
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void expandRightOrAggregate(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
//    info("expandRightOrAggregate(" + toString(previous) + ", " +toString(stateThatWillBeDeleted) + ", " +  toString(next) + ")");
    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWillBeDeleted.requireEmissionDate(),
            stateThatWillBeDeleted.requireSensorIdentifier(),
            true
    );

    if (nextTrue.isPresent()) {
      if (!areAgglomerated(stateThatWillBeDeleted, nextTrue.get())) {
        if (areAgglomerated(next, nextTrue.get())) {
          delete(stateThatWillBeDeleted);
          delete(nextTrue.get());
        } else {
          move(stateThatWillBeDeleted, next);
        }
      }
    } else {
      move(stateThatWillBeDeleted, next);
    }
  }

  /**
   * TRUE FALSE TRUE - MERGE
   *
   * @param previous
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void merge(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
//    info("merge(" + toString(previous) + ", " + toString(stateThatWillBeDeleted) + ", " +  toString(next) + ")");

    if (!areAgglomerated(stateThatWillBeDeleted, next)) {
      delete(stateThatWillBeDeleted);
      delete(next);
    }
  }

  /**
   * FALSE TRUE FALSE - DROP OR SPLIT
   * FALSE TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE NONE  - DROP OR SPLIT
   * NONE  TRUE FALSE - DROP OR SPLIT
   *
   * @param previous
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void dropOrSplit(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @Nullable final BooleanValueState next
  ) {
//    info("dropOrSplit(" + toString(previous) + ", " + toString(stateThatWillBeDeleted) + ", " +  toString(next) + ")");

    if (previous == null && next == null) {
      delete(stateThatWillBeDeleted);
    } else if (previous == null) {
      dropOrSplitNext(stateThatWillBeDeleted, next);
    } else if (next == null) {
      dropOrSplitPrevious(previous, stateThatWillBeDeleted);
    } else {
      dropOrSplitInner(previous, stateThatWillBeDeleted, next);
    }
  }

  /**
   * FALSE TRUE FALSE - DROP OR SPLIT
   *
   * @param previous
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void dropOrSplitInner (
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
//    info("dropOrSplitInner(" + toString(previous) + ", " + toString(stateThatWillBeDeleted) + ", " +  toString(next) + ")");

    @NonNull final Optional<BooleanValueState> previousTrue = _booleans.findPreviousWithValue(
            stateThatWillBeDeleted.requireEmissionDate(),
            stateThatWillBeDeleted.requireSensorIdentifier(),
            true
    );

    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWillBeDeleted.requireEmissionDate(),
            stateThatWillBeDeleted.requireSensorIdentifier(),
            true
    );

    if (previousTrue.isPresent() && nextTrue.isPresent()) {
      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWillBeDeleted.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (!areAgglomerated(previousFalse, nextTrue.get())) {
        if (areAgglomerated(previousFalse, stateThatWillBeDeleted)) {
          emit(previousFalse);
        } else {
          delete(stateThatWillBeDeleted);
        }

        if (areAgglomerated(next, nextTrue.get())) {
          emit(nextTrue.get());
        } else {
          delete(next);
        }
      }
    } else if (previousTrue.isPresent()) {
      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWillBeDeleted.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (areAgglomerated(previousFalse, stateThatWillBeDeleted)) {
        emit(previousFalse);
      } else {
        delete(stateThatWillBeDeleted);
      }

      delete(next);
    } else if (nextTrue.isPresent()) {
      delete(stateThatWillBeDeleted);

      if (areAgglomerated(next, nextTrue.get())) {
        emit(nextTrue.get());
      } else {
        delete(next);
      }
    } else {
      delete(stateThatWillBeDeleted);
      delete(next);
    }
  }

  /**
   * FALSE TRUE NONE  - DROP OR SPLIT
   *
   * @param previous
   * @param stateThatWillBeDeleted
   */
  private void dropOrSplitPrevious(
          @NonNull final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted
  ) {
//    info("dropOrSplitPrevious(" + toString(previous) + ", " + toString(stateThatWillBeDeleted) + ")");

    @NonNull final Optional<BooleanValueState> previousTrue = _booleans.findPreviousWithValue(
            stateThatWillBeDeleted.requireEmissionDate(),
            stateThatWillBeDeleted.requireSensorIdentifier(),
            true
    );

    if (previousTrue.isPresent()) {
      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWillBeDeleted.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (areAgglomerated(previousFalse, stateThatWillBeDeleted)) {
        emit(previousFalse);
      } else {
        delete(stateThatWillBeDeleted);
      }
    } else {
      delete(stateThatWillBeDeleted);
    }

  }

  /**
   * NONE  TRUE FALSE - DROP OR SPLIT
   *
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void dropOrSplitNext(
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
//    info("dropOrSplitNext(" + toString(stateThatWillBeDeleted) + ", " +  toString(next) + ")");

    @NonNull final Optional<BooleanValueState> nextTrue = _booleans.findNextWithValue(
            stateThatWillBeDeleted.requireEmissionDate(),
            stateThatWillBeDeleted.requireSensorIdentifier(),
            true
    );

    delete(stateThatWillBeDeleted);

    if (nextTrue.isPresent() && areAgglomerated(next, nextTrue.get())) {
      emit(nextTrue.get());
    } else {
      delete(next);
    }
  }

  /**
   * FALSE TRUE TRUE - REDUCE OR SPLIT
   * NONE  TRUE TRUE - REDUCE OR SPLIT
   *
   * @param previous
   * @param stateThatWillBeDeleted
   * @param next
   */
  private void reduceLeftOrSplit(
          @Nullable final BooleanValueState previous,
          @NonNull final BooleanValueState stateThatWillBeDeleted,
          @NonNull final BooleanValueState next
  ) {
//    info("reduceLeftOrSplit(" + toString(previous) + ", " +toString(stateThatWillBeDeleted) + ", " +  toString(next) + ")");
    @NonNull final Optional<BooleanValueState> previousTrue = _booleans.findPreviousWithValue(
            stateThatWillBeDeleted.requireEmissionDate(),
            stateThatWillBeDeleted.requireSensorIdentifier(),
            true
    );

    if (previousTrue.isPresent()) {
      @NonNull final BooleanValueState previousFalse = _booleans.findNextWithValue(
              previousTrue.get().requireEmissionDate(),
              stateThatWillBeDeleted.requireSensorIdentifier(),
              false
      ).orElseThrow();

      if (areAgglomerated(previousFalse, stateThatWillBeDeleted)) {
        if (!areAgglomerated(previousFalse, next)) {
          emit(previousFalse);
          emit(next);
        }
      } else {
        move(stateThatWillBeDeleted, next);
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

  private @NonNull Optional<BooleanValueState> getOutputFromInput(@NonNull final State input) {
    return _booleans.find(getSensor().orElseThrow().requireIdentifier(), input.requireEmissionDate());
  }

  private void delete(@NonNull final BooleanValueState state) {
//    info("delete(" + toString(state) + ")");

    if (state.requireSensorIdentifier().equals(getConfiguration().getSource())) {
//      info("delete (" + toString(state) + ") from input");
      delete(getOutputFromInput(state).orElseThrow());
    } else {
//      info("delete (" + toString(state) + ") from output");
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
