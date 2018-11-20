package org.liara.api.recognition.sensor.common.virtual.updown.label;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.LabelStateRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SapaRepositories;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher _publisher;

  @NonNull
  private final LabelStateRepository _outputs;

  private final SapaRepositories.@NonNull Boolean _inputs;

  @NonNull
  private final CorrelationRepository _correlations;

  @NonNull
  private final NodeRepository _nodes;

  @Autowired
  public UpDownToLabelSensor (
    @NonNull final ApplicationEventPublisher publisher, final SapaRepositories.@NonNull Boolean inputs,
    @NonNull final LabelStateRepository outputs,
    @NonNull final NodeRepository nodes,
    @NonNull final CorrelationRepository correlations
  )
  {
    _publisher = publisher;
    _inputs = inputs;
    _outputs = outputs;
    _nodes = nodes;
    _correlations = correlations;
  }

  public @NonNull UpDownToLabelSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(UpDownToLabelSensorConfiguration.class);
  }

  public @NonNull Sensor getSensor () {
    return getRunner().getSensor();
  }

  public @NonNull Long getInputSensor () {
    return getConfiguration().getInputSensor();
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  )
  {
    super.initialize(runner);

    @NonNull final List<ValueState.@NonNull Boolean> initializationStates = _inputs.find(getInputSensor(), Cursor.ALL);
    LabelState                                       current              = null;

    for (int index = 0; index < initializationStates.size(); ++index) {
      current = initialize(current, initializationStates.get(index));
    }
  }

  private @Nullable LabelState initialize (
    @Nullable final LabelState current, final ValueState.@NonNull Boolean next
  )
  {
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
  public void stateWasCreated (
    final StateEvent.@NonNull WasCreated event
  )
  {
    super.stateWasCreated(event);

    if (Objects.equals(event.getState().getSensorIdentifier(), getInputSensor())) {
      inputStateWasCreated((ValueState.Boolean) event.getState());
    }
  }

  public void inputStateWasCreated (final ValueState.@NonNull Boolean current) {
    @NonNull final Optional<LabelState> previous = _outputs.findAt(
      current.getEmissionDate(),
      getSensor().getIdentifier()
    );

    if (previous.isPresent() && previous.get().contains(current.getEmissionDate())) {
      onInnerInput(previous.get(), current);
    } else {
      onAloneInput(current);
    }
  }

  private void onAloneInput (
    final ValueState.@NonNull Boolean current
  )
  {
    if (current.getValue()) return;

    @NonNull final Optional<ValueState.Boolean> next = _inputs.findNext(current);

    if (!next.isPresent()) {
      begin(current);
    } else if (next.get().getValue()) {
      begin(
        current,
        findLabelStateCorrelatedWith(next.get().getIdentifier()).get()
      );
    } else {
      create(current, next.get());
    }
  }

  private void onInnerInput (
    @NonNull final LabelState previous, final ValueState.@NonNull Boolean current
  )
  {
    if (current.getValue()) return;

    @NonNull final State                        endState  = getEnd(previous).get();
    @NonNull final Optional<ValueState.Boolean> nextState = _inputs.findNext(current);

    finish(previous, current);

    if (endState == null) {
      if (nextState.isPresent()) {
        begin(nextState.get());
      }
    } else if (nextState.isPresent() && !Objects.equals(nextState.get(), endState)) {
      create(nextState.get(), endState);
    }
  }

  @Override
  public void stateWasMutated (
    final StateEvent.@NonNull WasMutated event
  )
  {
    super.stateWasMutated(event);

    if (Objects.equals(event.getNewValue().getSensorIdentifier(), getInputSensor())) {
      inputStateWasMutated((ValueState.Boolean) event.getOldValue(), (ValueState.Boolean) event.getNewValue());
    }
  }

  public void inputStateWasMutated (
    final ValueState.@NonNull Boolean previous, final ValueState.@NonNull Boolean next
  )
  {
    @NonNull final Optional<LabelState> correlated = findLabelStateCorrelatedWith(next.getIdentifier());

    if (!correlated.isPresent()) {
      inputStateWasCreated(next);
    } else if (!Objects.equals(next.getEmissionDate(), previous.getEmissionDate()) &&
               next.getValue() == previous.getValue()) {
      onBoundaryLocationChange(correlated.get(), next);
    } else if (next.getValue() != previous.getValue()) {
      onBoundaryTypeChange(correlated.get(), next);
    }
  }

  private void onBoundaryLocationChange (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean changed
  )
  {
    if (changed.equals(getStart(correlated))) {
      onStartBoundaryLocationChange(correlated, changed);
    } else {
      onEndBoundaryLocationChange(correlated, changed);
    }
  }

  private void onEndBoundaryLocationChange (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean changed
  )
  {
    final ValueState.@Nullable Boolean next = _inputs.findNext(correlated.getEnd(), changed.getSensorIdentifier())
                                                      .orElse(null);

    final ValueState.@Nullable Boolean previous = _inputs.findPrevious(
      correlated.getEnd(),
                                                                        changed.getSensorIdentifier()
    ).orElse(null);

    if (Objects.equals(next, changed) || Objects.equals(previous, changed)) {
      finish(correlated, changed);
    } else {
      resolveHardEndMutation(correlated, changed, next);
    }
  }

  private void onStartBoundaryLocationChange (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean changed
  )
  {
    final ValueState.@Nullable Boolean next = _inputs.findNext(correlated.getStart(), changed.getSensorIdentifier())
                                                      .orElse(null);

    final ValueState.Boolean previous = _inputs.findPrevious(correlated.getStart(), changed.getSensorIdentifier())
                                                .orElse(null);

    if (Objects.equals(next, changed) || Objects.equals(previous, changed)) {
      begin(changed, correlated);
    } else {
      resolveHardStartMutation(correlated, changed, next);
    }
  }

  private void onBoundaryTypeChange (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean changed
  )
  {
    if (Objects.equals(changed, getStart(correlated))) {
      onStartBoundaryTypeChange(correlated, changed);
    } else {
      onEndBoundaryTypeChange(correlated, changed);
    }
  }

  private void onEndBoundaryTypeChange (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean changed
  )
  {
    final ValueState.@Nullable Boolean next = _inputs.findNext(getEnd(correlated).get()).orElse(null);
    resolveHardEndMutation(correlated, changed, next);
  }

  private void onStartBoundaryTypeChange (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean changed
  )
  {
    final ValueState.@Nullable Boolean next = _inputs.findNext(correlated.getStart(), changed.getSensorIdentifier())
                                                      .orElse(null);
    resolveHardStartMutation(correlated, changed, next);
  }

  private void resolveHardStartMutation (
    @NonNull final LabelState correlated,
    final ValueState.@NonNull Boolean changed,
    final ValueState.@Nullable Boolean next
  )
  {
    if (next != null && next.getValue()) {
      begin(next, correlated);
    } else {
      delete(correlated);
    }

    inputStateWasCreated(changed);
  }


  private void resolveHardEndMutation (
    @NonNull final LabelState correlated,
    final ValueState.@NonNull Boolean changed,
    final ValueState.@Nullable Boolean next
  )
  {
    if (next != null && next.getValue()) {
      merge(
        correlated,
        findLabelStateCorrelatedWith(next.getIdentifier()).get()
      );
    } else {
      finish(correlated, next);
    }

    inputStateWasCreated(changed);
  }

  @Override
  public void stateWillBeDeleted (
    final StateEvent.@NonNull WillBeDeleted event
  )
  {
    super.stateWillBeDeleted(event);

    if (event.getState() instanceof ValueState.Boolean) {
      if (event.getState().getSensorIdentifier().equals(getInputSensor())) {
        inputStateWillBeDeleted((ValueState.Boolean) event.getState());
      }
    }
  }

  public void inputStateWillBeDeleted (
    final ValueState.@NonNull Boolean state
  )
  {
    @NonNull final Optional<LabelState> correlated = findLabelStateCorrelatedWith(state.getIdentifier());

    if (correlated.isPresent()) {
      onBoundaryDeletion(correlated.get(), state);
    }
  }

  private void onBoundaryDeletion (
    @NonNull final LabelState correlated, final ValueState.@NonNull Boolean state
  )
  {
    final ValueState.Boolean next = _inputs.findNext(state).orElse(null);

    if (Objects.equals(getEnd(correlated).get(), state)) {
      if (next != null && next.getValue()) {
        merge(
          correlated,
          findLabelStateCorrelatedWith(next.getIdentifier()).get()
        );
      } else {
        finish(correlated, next);
      }
    } else {
      if (next != null && next.getValue()) {
        begin(next, correlated);
      } else {
        delete(correlated);
      }
    }
  }

  private void merge (
    @NonNull final LabelState left, @NonNull final LabelState right
  )
  {
    delete(right);
    finish(left, getEnd(right).get());
  }

  private void delete (@NonNull final LabelState right) {
    _publisher.publishEvent(new ApplicationEntityEvent.Delete(this, right));
  }

  private void finish (
    @NonNull final LabelState current, @Nullable final State next
  )
  {
    @NonNull final LabelState  mutation       = Duplicator.duplicate(current);
    @NonNull final Correlation endCorrelation = getEndCorrelation(current).get();
    mutation.setEnd(next == null ? null : next.getEmissionDate());

    if (next == null) {
      _publisher.publishEvent(new ApplicationEntityEvent.Delete(endCorrelation));
      _publisher.publishEvent(new ApplicationEntityEvent.Update(current));
    } else {
      endCorrelation.setEndStateIdentifier(next.getIdentifier());
      _publisher.publishEvent(new ApplicationEntityEvent.Update(current, endCorrelation));
    }
  }

  private LabelState begin (@NonNull final State start) {
    return create(start, null);
  }

  private LabelState create (
    @NonNull final State start, @Nullable final State end
  )
  {
    @NonNull final LabelState state = new LabelState();

    state.setEmissionDate(start.getEmissionDate());
    state.setSensorIdentifier(getSensor().getIdentifier());
    state.setStart(start.getEmissionDate());
    state.setEnd(end == null ? null : end.getEmissionDate());

    _publisher.publishEvent(new ApplicationEntityEvent.Create(this, state));

    @NonNull final Correlation startCorrelation = new Correlation();

    startCorrelation.setName("start");
    startCorrelation.setStartStateIdentifier(state.getIdentifier());
    startCorrelation.setEndStateIdentifier(start.getIdentifier());

    _publisher.publishEvent(new ApplicationEntityEvent.Create(this, startCorrelation));

    if (end != null) {
      @NonNull final Correlation endCorrelation = new Correlation();

      startCorrelation.setName("end");
      startCorrelation.setStartStateIdentifier(state.getIdentifier());
      startCorrelation.setEndStateIdentifier(end.getIdentifier());

      _publisher.publishEvent(new ApplicationEntityEvent.Create(this, endCorrelation));
    }

    return state;
  }

  private void begin (
    final ValueState.@NonNull Boolean current, @NonNull final LabelState state
  )
  {
    @NonNull final LabelState mutation = Duplicator.duplicate(state);
    mutation.setStart(current.getEmissionDate());
    mutation.setEmissionDate(current.getEmissionDate());

    @NonNull final Correlation startCorrelation = getStartCorrelation(state);
    startCorrelation.setEndStateIdentifier(state.getIdentifier());

    _publisher.publishEvent(new ApplicationEntityEvent.Update(this, state, startCorrelation));
  }

  private @NonNull Correlation getStartCorrelation (@NonNull final LabelState label) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatStartBy(
      getSensor().getIdentifier(),
      "start",
      label.getIdentifier()
    ).get();
  }

  private ValueState.@NonNull Boolean getStart (@NonNull final LabelState label) {
    return _inputs.find(getStartCorrelation(label).getEndStateIdentifier()).get();
  }

  private @NonNull Optional<Correlation> getEndCorrelation (@NonNull final LabelState label) {
    return _correlations.findFirstCorrelationFromSeriesWithNameAndThatStartBy(
      getSensor().getIdentifier(),
      "end",
      label.getIdentifier()
    );
  }

  private @NonNull Optional<ValueState.Boolean> getEnd (@NonNull final LabelState label) {
    @NonNull final Optional<Correlation> correlation = getEndCorrelation(label);

    return correlation.isPresent() ? _inputs.find(correlation.get().getEndStateIdentifier())
                                   : Optional.empty();
  }

  private @NonNull Optional<LabelState> findLabelStateCorrelatedWith (
    @NonNull final Long state
  ) {
    @NonNull final Optional<Correlation> start = _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().getIdentifier(),
      "start",
      state
    );

    if (start.isPresent()) {
      return _outputs.find(start.get().getStartStateIdentifier());
    }

    @NonNull final Optional<Correlation> end = _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().getIdentifier(),
      "end",
      state
    );

    if (end.isPresent()) {
      return _outputs.find(end.get().getStartStateIdentifier());
    } else {
      return Optional.empty();
    }
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
}
