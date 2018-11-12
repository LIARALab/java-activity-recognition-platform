package org.liara.api.recognition.sensor.common.virtual.updown.label;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.LabelStateRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.utils.Duplicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@UseSensorConfigurationOfType(UpDownToLabelSensorConfiguration.class)
@EmitStateOfType(LabelState.class)
@Component
@Scope("prototype")
public class UpDownToLabelSensor
       extends AbstractVirtualSensorHandler
{
  @NonNull
  private final ApplicationEventPublisher _publisher;
  
  @NonNull
  private final LabelStateRepository _outputs;
  
  @NonNull
  private final ValueStateRepository<Boolean> _inputs;
  
  @NonNull
  private final NodeRepository _nodes;
  
  @Autowired
  public UpDownToLabelSensor (
    @NonNull final ApplicationEventPublisher publisher, @NonNull final ValueStateRepository inputs,
    @NonNull final LabelStateRepository outputs,
    @NonNull final NodeRepository nodes
  ) {
    _publisher = publisher;
    _inputs = inputs;
    _outputs = outputs;
    _nodes = nodes;
  }

  public @NonNull UpDownToLabelSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(UpDownToLabelSensorConfiguration.class
    );
  }

  public @NonNull Sensor getSensor () {
    return getRunner().getSensor();
  }

  public @NonNull ApplicationEntityReference<Sensor> getInputSensor () {
    return getConfiguration().getInputSensor();
  }
  
  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);

    @NonNull final List<@NonNull ValueState<Boolean>> initializationStates = _inputs.find(getInputSensor());
    LabelState                                        current              = null;
    
    for (int index = 0; index < initializationStates.size(); ++index) {
      current = initialize(current, initializationStates.get(index));
    }
  }

  private @Nullable LabelState initialize (
    @Nullable final LabelState current, @NonNull final ValueState<Boolean> next
  ) {
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
  ) {
    super.stateWasCreated(event);

    if (Objects.equals(event.getState().getSensorIdentifier(), getInputSensor())) {
      inputStateWasCreated((ValueState<Boolean>) event.getState());
    }
  }

  public void inputStateWasCreated (@NonNull final ValueState<Boolean> current) {
    @NonNull final Optional<LabelState> previous = _outputs.findAt(
      current.getEmissionDate(),
      getSensor().getReference()
    );

    if (previous.isPresent() && previous.get().contains(current.getEmissionDate())) {
      onInnerInput(previous.get(), current);
    } else {
      onAloneInput(current);
    }
  }

  private void onAloneInput (
    @NonNull final ValueState<Boolean> current
  ) {
    if (current.getValue()) return;

    @NonNull final Optional<ValueState<Boolean>> next = _inputs.findNext(current);

    if (!next.isPresent()) {
      begin(current);
    } else if (next.get().getValue()) {
      begin(current, findLabelStateThatStartBy(next.get().getReference())).get(0);
    } else {
      create(current, next.get());
    }
  }

  private void onInnerInput (
    @NonNull final LabelState previous, @NonNull final ValueState<Boolean> current
  ) {
    if (current.getValue()) return;

    @NonNull final State                         endState  = findEnd(previous).get();
    @NonNull final Optional<ValueState<Boolean>> nextState = _inputs.findNext(current);
    
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
  ) {
    super.stateWasMutated(event);

    if (Objects.equals(event.getNewValue().getSensorIdentifier(), getInputSensor())) {
      inputStateWasMutated((ValueState<Boolean>) event.getOldValue(), (ValueState<Boolean>) event.getNewValue()
      );
    }
  }
  
  public void inputStateWasMutated (
    @NonNull final ValueState<Boolean> previous, @NonNull final ValueState<Boolean> next
  ) {
    @NonNull final Optional<LabelState> correlated = findLabelStateThatStartOrEndBy(next.getReference(),
                                                                                    getSensor().getReference()
    );

    if (!correlated.isPresent()) {
      inputStateWasCreated(next);
    } else if (!Objects.equals(next.getEmissionDate(), previous.getEmissionDate()) &&
      next.getValue() == previous.getValue()
    ) {
      onBoundaryLocationChange(correlated.get(), next);
    } else if (next.getValue() != previous.getValue()) {
      onBoundaryTypeChange(correlated.get(), next);     
    }
  }

  private void onBoundaryLocationChange (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> changed
  ) {
    if (changed.equals(getStartOf(correlated))) {
      onStartBoundaryLocationChange(correlated, changed);
    } else {
      onEndBoundaryLocationChange(correlated, changed);
    }
  }

  private void onEndBoundaryLocationChange (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> changed
  ) {
    @Nullable final ValueState<Boolean> next = _inputs.findNext(correlated.getEnd(), changed.getSensorIdentifier()
    ).orElse(null);

    @Nullable final ValueState<Boolean> previous = _inputs.findPrevious(
      correlated.getEnd(), changed.getSensorIdentifier()
    ).orElse(null);
    
    if (Objects.equals(next, changed) || Objects.equals(previous, changed)) {
      finish(correlated, changed);
    } else  {
      resolveHardEndMutation(correlated, changed, next);
    }
  }

  private void onStartBoundaryLocationChange (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> changed
  ) {
    @Nullable final ValueState<Boolean> next = _inputs.findNext(
      correlated.getStart(), changed.getSensorIdentifier()
    ).orElse(null);

    final ValueState<Boolean> previous = _inputs.findPrevious(
      correlated.getStart(), changed.getSensorIdentifier()
    ).orElse(null);
    
    if (Objects.equals(next, changed) || Objects.equals(previous, changed)) {
      begin(changed, correlated);
    } else  {
      resolveHardStartMutation(correlated, changed, next);
    }
  }

  private void onBoundaryTypeChange (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> changed
  ) {
    if (Objects.equals(changed, getStartOf(correlated))) {
      onStartBoundaryTypeChange(correlated, changed);
    } else {
      onEndBoundaryTypeChange(correlated, changed);
    }
  }

  private void onEndBoundaryTypeChange (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> changed
  ) {
    @Nullable final ValueState<Boolean> next = _inputs.findNext(getEndOf(correlated)).orElse(null);
    resolveHardEndMutation(correlated, changed, next);
  }

  private void onStartBoundaryTypeChange (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> changed
  ) {
    @Nullable final ValueState<Boolean> next = _inputs.findNext(
      correlated.getStart(), changed.getSensorIdentifier()
    ).orElse(null);
    resolveHardStartMutation(correlated, changed, next);
  }

  private void resolveHardStartMutation (
    @NonNull final LabelState correlated,
    @NonNull final ValueState<Boolean> changed,
    @Nullable final ValueState<Boolean> next
  ) {
    if (next != null && next.getValue()) {
      begin(next, correlated);
    } else {
      delete(correlated);
    }
    
    inputStateWasCreated(changed);
  }


  private void resolveHardEndMutation (
    @NonNull final LabelState correlated,
    @NonNull final ValueState<Boolean> changed,
    @Nullable final ValueState<Boolean> next
  ) {
    if (next != null && next.getValue()) {
      merge(correlated, _outputs.findFirstWithAnyCorrelation(
        Arrays.asList("start", "end"),
        ApplicationEntityReference.of(next), 
        ApplicationEntityReference.of(getSensor())
      ).get());
    } else {
      finish(correlated, next);
    }
    
    inputStateWasCreated(changed);
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateEvent.WillBeDeleted event
  ) {
    super.stateWillBeDeleted(event);

    if (event.getState() instanceof ValueState.Boolean) {
      if (event.getState().getSensorIdentifier().equals(getInputSensor())) {
        inputStateWillBeDeleted((ValueState.Boolean) event.getState());
      }
    }
  }

  public void inputStateWillBeDeleted (
    @NonNull final ValueState<Boolean> state
  ) {
    @NonNull final Optional<LabelState> correlated = _outputs.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"),
      ApplicationEntityReference.of(state), 
      ApplicationEntityReference.of(getSensor())
    );
    
    if (correlated.isPresent()) {
      onBoundaryDeletion(correlated.get(), state);
    }
  }

  private void onBoundaryDeletion (
    @NonNull final LabelState correlated, @NonNull final ValueState<Boolean> state
  ) {
    final ValueState<Boolean> next = _inputs.findNext(state).orElse(null);

    if (Objects.equals(getEndOf(correlated), state)) {
      if (next != null && next.getValue()) {
        merge(correlated, _outputs.findFirstWithAnyCorrelation(
          Arrays.asList("start", "end"),
          ApplicationEntityReference.of(next), 
          ApplicationEntityReference.of(getSensor())
        ).get());
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
  ) {
    @NonNull final State end = getEndOf(right);
    delete(right);
    finish(left, end);
  }

  private void delete (@NonNull final LabelState right) {
    _publisher.publishEvent(new ApplicationEntityEvent.Delete(this, right));
  }

  private void finish (
    @NonNull final LabelState current,
    @Nullable final State next
  ) {
    @NonNull final LabelState mutation = Duplicator.duplicate(current);
    
    if (next == null) {
      mutation.setEnd(null);
      mutation.decorrelate("end");
    } else {
      mutation.setEnd(next.getEmissionDate());
      mutation.correlate("end", next);
    }

    mutation.setState(current.getReference());
    
    _manager.execute(mutation);
  }

  private LabelState begin (@NonNull final State start) {
    return create(start, null);
  }

  private LabelState create (
    @NonNull final State start, 
    @Nullable final State end
  ) {
    final LabelStateCreationSchema creation = new LabelStateCreationSchema();
    creation.setEmittionDate(start.getEmissionDate());
    creation.setNode(getActivatedNode().getReference());
    creation.setSensor(getSensor().getReference());
    creation.setStart(start.getEmissionDate());
    creation.correlate("start", start);
    
    if (end != null) {
      creation.setEnd(end.getEmissionDate());
      creation.correlate("end", end);
    }
    
    return _manager.execute(creation);
  }
  
  private void begin (
    @NonNull final ValueState<Boolean> current, @NonNull final LabelState state
  ) {
    final LabelStateMutationSchema schema = new LabelStateMutationSchema();
    schema.setState(state.getReference());
    schema.setStart(current.getEmissionDate());
    schema.setEmittionDate(current.getEmissionDate());
    schema.correlate("start", current);
    
    _manager.execute(schema);
  }
}
