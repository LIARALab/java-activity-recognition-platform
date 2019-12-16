package org.liara.api.recognition.labellizer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.AnyStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.LabelStateRepository;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillDeleteStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UpDownToLabelSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType
{
  @NonNull
  private final String START_LABEL = "start";

  @NonNull
  private final String END_LABEL = "end";

  @NonNull
  private final APIEventPublisher _publisher;

  @NonNull
  private final LabelStateRepository _outputs;

  @NonNull
  private final AnyStateRepository _inputs;

  @NonNull
  private final CorrelationRepository _correlations;

  @Autowired
  public UpDownToLabelSensor (@NonNull final UpDownToLabelSensorBuilder builder) {
    _publisher = Objects.requireNonNull(builder.getPublisher());
    _inputs = Objects.requireNonNull(builder.getInputs());
    _outputs = Objects.requireNonNull(builder.getOutputs());
    _correlations = Objects.requireNonNull(builder.getCorrelations());
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

    @NonNull final List<@NonNull State> initializationStates = (
      _inputs.find(getInputSensorIdentifier(), Cursor.ALL)
    );

    @Nullable LabelState current = null;

    for (@NonNull final State state : initializationStates) {
      current = initialize(current, cast(state));
    }
  }

  private @Nullable LabelState initialize (
    @Nullable final LabelState current,
    @NonNull final BooleanValueState next
  ) {
    if (next.requireValue() && current == null) {
      return create(next, null);
    } else if (!next.requireValue() && current != null) {
      finish(current, next);
      return null;
    } else {
      return current;
    }
  }

  /**
   * @see AbstractVirtualSensorHandler#stateWasCreated(DidCreateStateEvent)
   */
  @Override
  public void stateWasCreated (@NonNull final DidCreateStateEvent event) {
    super.stateWasCreated(event);

    @NonNull final State stateThatWasCreated = event.getState();

    if (Objects.equals(stateThatWasCreated.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWasCreated(cast(stateThatWasCreated));
    }
  }

  /**
   * PREVIOUS; CREATED; NEXT ; ACTION NONE    ; TRUE   ; NONE ; BEGIN(CREATED) NONE    ; TRUE   ;
   * TRUE ; BEGIN(NEXT, CREATED) NONE    ; TRUE   ; FALSE; CREATE(CREATED, NEXT) NONE    ; FALSE  ;
   * NONE ; NOTHING NONE    ; FALSE  ; TRUE ; NOTHING NONE    ; FALSE  ; FALSE; NOTHING TRUE    ;
   * TRUE   ; NONE ; NOTHING TRUE    ; TRUE   ; TRUE ; NOTHING TRUE    ; TRUE   ; FALSE; NOTHING
   * TRUE    ; FALSE  ; NONE ; END(PREVIOUS, CREATED) TRUE    ; FALSE  ; TRUE ; SPLIT(CREATED, NEXT)
   * TRUE    ; FALSE  ; FALSE; END(PREVIOUS, CREATED) FALSE   ; TRUE   ; NONE ; BEGIN(CREATED) FALSE
   *   ; TRUE   ; TRUE ; BEGIN(NEXT, CREATED) FALSE   ; TRUE   ; FALSE; CREATE(CREATED, NEXT) FALSE
   * ; FALSE  ; NONE ; NOTHING FALSE   ; FALSE  ; TRUE ; NOTHING FALSE   ; FALSE  ; FALSE; NOTHING
   *
   * @param stateThatWasCreated The state that was inserted into the database.
   */
  private void inputStateWasCreated (@NonNull final BooleanValueState stateThatWasCreated) {
    @NonNull final Optional<BooleanValueState> previous = (
      _inputs.findPrevious(stateThatWasCreated).map(this::cast)
    );

    if (!previous.isPresent() || !previous.get().requireValue()) {
      if (stateThatWasCreated.requireValue()) {
        @NonNull final Optional<BooleanValueState> next = (
          _inputs.findNext(stateThatWasCreated).map(this::cast)
        );

        if (!next.isPresent()) {
          begin(stateThatWasCreated);
        } else if (next.get().requireValue()) {
          begin(next.get(), stateThatWasCreated);
        } else {
          create(stateThatWasCreated, next.get());
        }
      }
    } else {
      if (!stateThatWasCreated.requireValue()) {
        @NonNull final Optional<BooleanValueState> next = (
          _inputs.findNext(stateThatWasCreated).map(this::cast)
        );

        if (next.isPresent() && next.get().requireValue()) {
          split(stateThatWasCreated, next.get());
        } else {
          finish(previous.get(), stateThatWasCreated);
        }
      }
    }
  }

  /**
   * @see AbstractVirtualSensorHandler#stateWillBeMutated(WillUpdateStateEvent)
   */
  @Override
  public void stateWillBeMutated (@NonNull final WillUpdateStateEvent event) {
    super.stateWillBeMutated(event);

    @NonNull final State stateThatWillBeMutated = event.getOldValue();

    if (Objects.equals(stateThatWillBeMutated.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWillBeDeleted(cast(stateThatWillBeMutated));
    }
  }

  /**
   * @see AbstractVirtualSensorHandler#stateWasMutated(DidUpdateStateEvent)
   */
  @Override
  public void stateWasMutated (@NonNull final DidUpdateStateEvent event) {
    super.stateWasMutated(event);

    @NonNull final State stateThatWasMutated = event.getNewValue();

    if (Objects.equals(stateThatWasMutated.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWasCreated(cast(stateThatWasMutated));
    }
  }

  /**
   * @see AbstractVirtualSensorHandler#stateWillBeDeleted(WillDeleteStateEvent)
   */
  @Override
  public void stateWillBeDeleted (@NonNull final WillDeleteStateEvent event) {
    super.stateWillBeDeleted(event);

    @NonNull final State stateThatWillBeDeleted = event.getState();

    if (Objects.equals(stateThatWillBeDeleted.getSensorIdentifier(), getInputSensorIdentifier())) {
      inputStateWillBeDeleted(cast(stateThatWillBeDeleted));
    }
  }

  /**
   * PREVIOUS; DELETED; NEXT ; ACTION
   * NONE    ; TRUE   ; NONE ; DELETE(DELETED)
   * NONE    ; TRUE   ; TRUE ; BEGIN(DELETED, NEXT)
   * NONE    ; TRUE   ; FALSE; DELETE(DELETED)
   * NONE    ; FALSE  ; NONE ; NOTHING
   * NONE    ; FALSE  ; TRUE ; NOTHING
   * NONE    ; FALSE  ; FALSE; NOTHING
   * TRUE    ; TRUE   ; NONE ; NOTHING
   * TRUE    ; TRUE   ; TRUE ; NOTHING
   * TRUE    ; TRUE   ; FALSE; NOTHING
   * TRUE    ; FALSE  ; NONE ; FINISH(PREVIOUS, NULL)
   * TRUE    ; FALSE  ; TRUE ; MERGE(PREVIOUS, NEXT)
   * TRUE    ; FALSE  ; FALSE; FINISH(PREVIOUS, NEXT)
   * FALSE   ; TRUE   ; NONE ; DELETE(DELETED)
   * FALSE   ; TRUE   ; TRUE ; BEGIN(DELETED, NEXT)
   * FALSE   ; TRUE   ; FALSE; DELETE(DELETED)
   * FALSE   ; FALSE  ; NONE ; NOTHING
   * FALSE   ; FALSE  ; TRUE ; NOTHING
   * FALSE   ; FALSE  ; FALSE; NOTHING
   *
   * @param stateThatWillBeDeleted The state that will be removed from the database.
   */
  private void inputStateWillBeDeleted (@NonNull final BooleanValueState stateThatWillBeDeleted) {
    @NonNull final Optional<BooleanValueState> previous = (
      _inputs.findPrevious(stateThatWillBeDeleted).map(this::cast)
    );

    if (!previous.isPresent() || !previous.get().requireValue()) {
      if (stateThatWillBeDeleted.requireValue()) {
        @NonNull final Optional<BooleanValueState> next = (
          _inputs.findNext(stateThatWillBeDeleted).map(this::cast)
        );

        if (!next.isPresent() || !next.get().requireValue()) {
          delete(stateThatWillBeDeleted);
        } else {
          begin(stateThatWillBeDeleted, next.get());
        }
      }
    } else {
      if (!stateThatWillBeDeleted.requireValue()) {
        @NonNull final Optional<BooleanValueState> next = (
          _inputs.findNext(stateThatWillBeDeleted).map(this::cast)
        );

        if (next.isPresent() && next.get().requireValue()) {
          merge(previous.get(), next.get());
        } else {
          finish(previous.get(), next.orElse(null));
        }
      }
    }
  }

  /**
   * Merge the label that contains the left state with the label that contains the right state.
   *
   * @param left Inner state of the left label to merge.
   * @param right Inner state of the right label to merge.
   */
  private void merge (
    @NonNull final BooleanValueState left,
    @NonNull final BooleanValueState right
  ) {
    @NonNull final LabelState leftLabel = (
      findLabelStateAt(left.requireEmissionDate()).orElseThrow()
    );

    @NonNull final LabelState rightLabel = (
      findLabelStateAt(right.requireEmissionDate()).orElseThrow()
    );

    @NonNull final Optional<BooleanValueState> oldEnd = (
      findEndState(Objects.requireNonNull(rightLabel.getIdentifier()))
    );

    delete(rightLabel);
    finish(leftLabel, oldEnd.orElse(null));
  }

  /**
   * Delete the given label state.
   *
   * @param state The label state to delete.
   */
  private void delete (@NonNull final LabelState state) {
    _publisher.delete(getStartCorrelation(Objects.requireNonNull(state.getIdentifier())));
    findEndCorrelation(state.getIdentifier()).ifPresent(_publisher::delete);
    _publisher.delete(state);
  }

  /**
   * Delete the label that contains the given state.
   *
   * @param inner Inner state of the label to delete.
   */
  private void delete (@NonNull final State inner) {
    delete(findLabelStateAt(inner.requireEmissionDate()).orElseThrow());
  }

  /**
   * Splits the label in two parts by using the given pair of states.
   *
   * The first label will begins from the start of the label that contains the split state and
   * finish at the split state. Thee second one will begins from the given next state and finish to
   * the end state of the label that contains the split state.
   *
   * @param split The state to use for splitting.
   * @param next The next state to use as a starting state of the new state to create.
   */
  private void split (
    @NonNull final BooleanValueState split,
    @NonNull final BooleanValueState next
  ) {
    @NonNull final LabelState origin = findLabelStateAt(split.requireEmissionDate()).orElseThrow();
    @NonNull final Optional<BooleanValueState> oldEnd = (
      findEndState(origin.requireIdentifier())
    );

    finish(origin, split);
    create(next, oldEnd.orElse(null));
  }

  /**
   * Finish the given label at the given state.
   *
   * The ending state parameter is optional, if no ending state was passed to this method the
   * resulting label may pass from a terminate state to a non-finite state.
   *
   * @param label The label to mutate.
   * @param ending The new ending state of the given label.
   *
   * @return The mutated label.
   */
  private @NonNull LabelState finish (
    @NonNull final LabelState label,
    @Nullable final BooleanValueState ending
  ) {
    @NonNull final LabelState mutation = Duplicator.duplicate(label);

    mutation.setEnd(ending == null ? null : ending.getEmissionDate());

    _publisher.update(mutation);

    @NonNull final Optional<Correlation> endCorrelation = (
      findEndCorrelation(Objects.requireNonNull(mutation.getIdentifier()))
    );

    if (ending == null) {
      endCorrelation.ifPresent(_publisher::delete);
    } else if (endCorrelation.isPresent()) {
      @NonNull final Correlation correlation = Duplicator.duplicate(endCorrelation.get());
      correlation.setEndStateIdentifier(ending.getIdentifier());
      _publisher.update(correlation);
    } else {
      @NonNull final Correlation correlation = new Correlation();
      correlation.setName(END_LABEL);
      correlation.setStartStateIdentifier(mutation.getIdentifier());
      correlation.setEndStateIdentifier(ending.getIdentifier());
      _publisher.create(correlation);
    }

    return mutation;
  }

  /**
   * Ending the label that contains the given state to the given ending state.
   *
   * The ending state parameter is optional, if no ending state was passed to this method the
   * resulting label may pass from a terminate state to a non-finite state.
   *
   * @param inner A state that the label to update contain.
   * @param ending The new ending state of the label.
   *
   * @return The mutated label.
   */
  private @NonNull LabelState finish (
    @NonNull final BooleanValueState inner,
    @Nullable final BooleanValueState ending
  ) {
    return finish(findLabelStateAt(inner.requireEmissionDate()).orElseThrow(), ending);
  }

  /**
   * Create a label that begin at the given start state and that end at the given end state.
   * <p>
   * The end state parameter is optional, if null is passed the resulting label will be non-finite :
   * it will start at the given start state but it will not terminate.
   * <p>
   * This method also create all the needed correlations.
   *
   * @param start The beginning state of the label to create.
   * @param end   The ending state of the label to create, may be null.
   *
   * @return The created label.
   */
  private @NonNull LabelState create (
    @NonNull final BooleanValueState start,
    @Nullable final BooleanValueState end
  ) {
    @NonNull final LabelState label = new LabelState();
    label.setName(getConfiguration().getLabel());
    label.setEmissionDate(start.getEmissionDate());
    label.setSensorIdentifier(getOutputSensorIdentifier());
    label.setStart(start.getEmissionDate());
    label.setEnd(end == null ? null : end.getEmissionDate());

    _publisher.create(label);

    @NonNull final Correlation startCorrelation = new Correlation();

    startCorrelation.setName(START_LABEL);
    startCorrelation.setStartStateIdentifier(label.getIdentifier());
    startCorrelation.setEndStateIdentifier(start.getIdentifier());

    _publisher.create(startCorrelation);

    if (end != null) {
      @NonNull final Correlation endCorrelation = new Correlation();

      endCorrelation.setName(END_LABEL);
      endCorrelation.setStartStateIdentifier(label.getIdentifier());
      endCorrelation.setEndStateIdentifier(end.getIdentifier());

      _publisher.create(endCorrelation);
    }

    return label;
  }

  /**
   * Create a non-finite label that starts from the given state.
   *
   * Alias of #create(start, null)
   *
   * @param start The beginning state of the label to create.
   */
  private @NonNull LabelState begin (@NonNull final BooleanValueState start) {
    return create(start, null);
  }

  /**
   * Extend the label that contains the given state such as it then begins from the new state.
   *
   * @param inner State that the label to mutate contains.
   * @param newStart New starting state of the label to mutate.
   *
   * @return The mutated label.
   */
  private @NonNull LabelState begin (
    @NonNull final BooleanValueState inner,
    @NonNull final BooleanValueState newStart
  ) {
    @NonNull final LabelState origin = findLabelStateAt(inner.requireEmissionDate()).orElseThrow();

    @NonNull final LabelState mutation = Duplicator.duplicate(origin);
    mutation.setStart(newStart.getEmissionDate());
    mutation.setEmissionDate(newStart.getEmissionDate());

    _publisher.update(mutation);

    @NonNull final Correlation startCorrelation = Duplicator.duplicate(
      getStartCorrelation(Objects.requireNonNull(origin.getIdentifier()))
    );
    startCorrelation.setEndStateIdentifier(newStart.getIdentifier());

    _publisher.update(startCorrelation);

    return mutation;
  }

  /**
   * Return the correlation between the given label and it's starting state.
   *
   * @param labelIdentifier Identifier of the label to fetch.
   *
   * @return The correlation between the given label and it's starting state.
   */
  private @NonNull Correlation getStartCorrelation (@NonNull final Long labelIdentifier) {
    return _correlations.findFirstCorrelationWithNameAndThatStartBy(START_LABEL, labelIdentifier)
                        .orElseThrow();
  }

  /**
   * Return the correlation between the given label and it's ending state.
   *
   * @param labelIdentifier Identifier of the label to fetch.
   *
   * @return The correlation between the given label and it's starting state.
   */
  private @NonNull Optional<Correlation> findEndCorrelation (@NonNull final Long labelIdentifier) {
    return _correlations.findFirstCorrelationWithNameAndThatStartBy(END_LABEL, labelIdentifier);
  }

  /**
   * Return the ending state of the given label.
   *
   * @param labelIdentifier Identifier of the label to fetch.
   *
   * @return The ending state of the given label.
   */
  private @NonNull Optional<BooleanValueState> findEndState (@NonNull final Long labelIdentifier) {
    return findEndCorrelation(labelIdentifier).map(Correlation::getEndStateIdentifier)
                                              .flatMap(_inputs::find)
                                              .map(this::cast);
  }

  private @NonNull Optional<LabelState> findLabelStateAt (@NonNull final ZonedDateTime dateTime) {
    return _outputs.findAt(dateTime, getOutputSensorIdentifier());
  }

  private @NonNull Optional<LabelState> findLabelStateBefore (
    @NonNull final ZonedDateTime dateTime
  ) {
    return _outputs.findPrevious(dateTime, getOutputSensorIdentifier());
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
