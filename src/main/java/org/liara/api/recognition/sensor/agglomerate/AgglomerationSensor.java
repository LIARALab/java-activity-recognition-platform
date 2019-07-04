package org.liara.api.recognition.sensor.agglomerate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.BooleanValueState;
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
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AgglomerationSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType
{
  @NonNull
  private final APIEventPublisher _apiEventPublisher;

  @NonNull
  private final BooleanValueStateRepository _booleanValues;

  @NonNull
  private final CorrelationRepository _correlations;

  public AgglomerationSensor (@NonNull final AgglomerationSensorBuilder builder) {
    _apiEventPublisher = Objects.requireNonNull(builder.getApiEventPublisher());
    _booleanValues = Objects.requireNonNull(builder.getBooleanValues());
    _correlations = Objects.requireNonNull(builder.getCorrelations());
  }

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);

    @NonNull final Duration duration = Duration.ofMillis(getConfiguration().getDuration());
    @NonNull final Iterator<@NonNull BooleanValueState> states = _booleanValues.find(
      getConfiguration().getSource(), Cursor.ALL
    ).iterator();
    @NonNull BooleanValueState lastChange;

    if (!states.hasNext()) return;

    do {
      lastChange = states.next();
    } while (Objects.equals(lastChange.getValue(), false) && states.hasNext());

    if (Objects.equals(lastChange.getValue(), true)) {
      emit(lastChange);
    }

    while (states.hasNext()) {
      @NonNull final BooleanValueState state = states.next();

      if (!Objects.equals(lastChange.getValue(), state.getValue())) {
        if (Objects.equals(state.getValue(), true) && !areAgglomerated(lastChange, state)) {
          emit(state);
          emit(lastChange);
        }

        lastChange = state;
      }
    }


    if (Objects.equals(lastChange.getValue(), false)) {
      emit(lastChange);
    }
  }

  public boolean match (@NonNull final State state) {
    return Objects.equals(state.getSensorIdentifier(), getConfiguration().getSource());
  }

  @Override
  public void stateWasCreated (@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (match(stateThatWasCreated)) {
      sourceStateWasCreated((BooleanValueState) stateThatWasCreated);
    }
  }

  /**
   * FALSE FALSE FALSE - NOTHING FALSE FALSE TRUE  - NOTHING FALSE FALSE NONE  - NOTHING FALSE TRUE
   * FALSE - CREATE OR  AGGLOMERATE FALSE TRUE  NONE  - CREATE OR  AGGLOMERATE FALSE TRUE  TRUE  -
   * EXPAND OR  AGGLOMERATE TRUE  TRUE  FALSE - NOTHING TRUE  TRUE  TRUE  - NOTHING TRUE  TRUE  NONE
   *  - NOTHING TRUE  FALSE FALSE - REDUCE MAY DISAGGREGATE TRUE  FALSE TRUE  - SPLIT  OR
   * AGGLOMERATE TRUE  FALSE NONE  - CREATE NONE  FALSE FALSE - NOTHING NONE  FALSE TRUE  - NOTHING
   * NONE  FALSE NONE  - NOTHING NONE  TRUE  FALSE - CREATE OR  AGGLOMERATE NONE  TRUE  TRUE  -
   * EXPAND NONE  TRUE  NONE  - CREATE
   *
   * @param stateThatWasCreated
   */
  private void sourceStateWasCreated (@NonNull final BooleanValueState stateThatWasCreated) {
    @NonNull final Optional<BooleanValueState> previous = _booleanValues.findPrevious(
      stateThatWasCreated
    );

    if (previous.isPresent()) {
      if (!Objects.equals(previous.get(), stateThatWasCreated)) {
        @NonNull final Optional<BooleanValueState> next = _booleanValues.findNext(
          stateThatWasCreated
        );

        if (Objects.equals(stateThatWasCreated.getValue(), true)) {
          if (next.isPresent() && Objects.equals(next.get().getValue(), true)) {
            expandOrAgglomerate(next.get(), stateThatWasCreated);
          } else {
            createOrAgglomerate(stateThatWasCreated, next.orElse(null));
          }
        } else if (!next.isPresent()) {
          emit(stateThatWasCreated);
        } else if (Objects.equals(next.get().getValue(), true)) {
          splitOrAgglomerate(stateThatWasCreated, next.get());
        } else {
          reduceOrDisaggregate(next.get(), stateThatWasCreated);
        }
      } // else nothing
    } else if (Objects.equals(stateThatWasCreated.getValue(), true)) {
      @NonNull final Optional<BooleanValueState> next = _booleanValues.findNext(
        stateThatWasCreated
      );

      if (next.isPresent()) {
        if (Objects.equals(stateThatWasCreated.getValue(), next.get().getValue())) {
          move(next.get(), stateThatWasCreated);
        } else {
          createOrAgglomerate(stateThatWasCreated, next.get());
        }
      } else {
        emit(stateThatWasCreated);
      }
    } // else nothing
  }

  private void splitOrAgglomerate (
    @NonNull final BooleanValueState stateThatWasCreated,
    @NonNull final BooleanValueState next
  ) {
    if (!areAgglomerated(stateThatWasCreated, next)) {
      emit(stateThatWasCreated);
      emit(next);
    }
  }

  private void reduceOrDisaggregate (
    @NonNull final BooleanValueState from,
    @NonNull final BooleanValueState to
  ) {
    @NonNull final Optional<BooleanValueState> next = _booleanValues.findNextWithValue(
      from.getEmissionDate(), from.getSensorIdentifier(), true
    );

    if (next.isPresent() && areAgglomerated(from, next.get())) {
      if (!areAgglomerated(to, next.get())) {
        emit(to);
        emit(next.get());
      }
    } else {
      move(from, to);
    }
  }

  private void expandOrAgglomerate (
    @NonNull final BooleanValueState from,
    @NonNull final BooleanValueState to
  ) {
    @NonNull final Optional<BooleanValueState> previous = _booleanValues.findPrevious(
      from.getEmissionDate(), getConfiguration().getSource()
    );

    // expansion into an existing agglomeration
    if (previous.isPresent() && Objects.equals(previous.get().getValue(), true)) {
      return;
    }

    if (previous.isPresent() && areAgglomerated(previous.get(), to)) {
      delete(previous.get());
      delete(getOutputFromInput(from));
    } else {
      move(from, to);
    }
  }

  private void createOrAgglomerate (
    @NonNull final BooleanValueState start,
    @Nullable final BooleanValueState end
  ) {
    @NonNull final Optional<BooleanValueState> previous = _booleanValues.findPrevious(
      start.getEmissionDate(), getConfiguration().getSource()
    );

    // created into an existing agglomeration
    if (previous.isPresent() && Objects.equals(previous.get().getValue(), true)) {
      return;
    }

    // created between two agglomeration or after an existing agglomeration
    if (previous.isPresent() && areAgglomerated(previous.get(), start)) {
      delete(previous.get());
    } else {
      emit(start);
    }

    if (end != null) {
      @NonNull final Optional<BooleanValueState> next = _booleanValues.findNext(
        end.getEmissionDate(), getConfiguration().getSource()
      );

      if (next.isPresent() && areAgglomerated(end, next.get())) {
        delete(next.get());
      } else {
        emit(end);
      }
    }
  }

  private boolean areAgglomerated (@NonNull final State first, @NonNull final State second) {
    return Duration.between(first.getEmissionDate(), second.getEmissionDate()).compareTo(
      Duration.ofMillis(getConfiguration().getDuration())
    ) <= 0;
  }

  @Override
  public void stateWillBeMutated (@NonNull final WillUpdateStateEvent event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (match(stateThatWillBeMutated)) {
      sourceStateWillBeDeleted((BooleanValueState) stateThatWillBeMutated);
    }
  }

  @Override
  public void stateWasMutated (@NonNull final DidUpdateStateEvent event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (match(stateThatWasMutated)) {
      sourceStateWasCreated((BooleanValueState) stateThatWasMutated);
    }
  }

  @Override
  public void stateWillBeDeleted (@NonNull final WillDeleteStateEvent event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (match(stateThatWillBeDeleted)) {
      sourceStateWillBeDeleted((BooleanValueState) stateThatWillBeDeleted);
    }
  }

  private void sourceStateWillBeDeleted (@NonNull final BooleanValueState stateThatWillBeDeleted) {

  }

  private @NonNull BooleanValueState getInputFromOutput (
    @NonNull final BooleanValueState output
  ) { }

  private BooleanValueState getOutputFromInput (@NonNull final BooleanValueState from) {
  }

  private void delete (@NonNull final BooleanValueState previousOutput) {
  }

  private void move (
    @NonNull final BooleanValueState from,
    @NonNull final BooleanValueState to
  ) {
  }

  private void emit (@NonNull final BooleanValueState state) {
  }

  public @NonNull AgglomerationSensorConfiguration getConfiguration () {
    return getConfiguration(AgglomerationSensorConfiguration.class).orElseThrow();
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return BooleanValueState.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return AgglomerationSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:agglomerate";
  }
}
