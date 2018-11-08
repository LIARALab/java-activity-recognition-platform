package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.LabelStateRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.api.data.schema.ActivationStateCreationSchema;
import org.liara.api.data.schema.ActivationStateMutationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.data.schema.StateDeletionSchema;
import org.liara.api.event.StateEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@UseSensorConfigurationOfType(UpDownToActivationSensorConfiguration.class)
@EmitStateOfType(ActivationState.class)
@Component
@Scope("prototype")
public class UpDownToActivationSensor 
       extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _manager;
  
  @NonNull
  private final LabelStateRepository _outputs;
  
  @NonNull
  private final ValueStateRepository _inputs;
  
  @NonNull
  private final NodeRepository _nodes;
  
  @Autowired
  public UpDownToActivationSensor (
    @NonNull final SchemaManager manager, @NonNull final ValueStateRepository inputs,
    @NonNull final LabelStateRepository outputs,
    @NonNull final NodeRepository nodes
  ) {
    _manager = manager;
    _inputs = inputs;
    _outputs = outputs;
    _nodes = nodes;
  }
  
  public UpDownToActivationSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      UpDownToActivationSensorConfiguration.class
    );
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  public ApplicationEntityReference<Sensor> getInputSensor () {
    return getConfiguration().getInputSensor();
  }
  
  public Node getActivatedNode () {
    final ApplicationEntityReference<Node> result = getConfiguration().getActivatedNode();

    return (result.isNull()) ? getRunner().getSensor().getNode()
                             : _nodes.find(result).get();
  }
  
  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);
    
    final List<BooleanState> initializationStates = _inputs.findAll(getInputSensor());
    ActivationState          current              = null;
    
    _manager.flush();
    _manager.clear();
    
    for (int index = 0; index < initializationStates.size(); ++index) {
      current = initialize(current, initializationStates.get(index));
    }
  }

  private ActivationState initialize (
    @Nullable final ActivationState current, 
    @NonNull final BooleanState next
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
    @NonNull final StateEvent event
  ) {
    super.stateWasCreated(event);

    if (Objects.equals(event.getState().getSensor(), getInputSensor())) {
      inputStateWasCreated((BooleanState) event.getState());
    }
  }

  public void inputStateWasCreated (@NonNull final BooleanState current) {
    final Optional<ActivationState> previous = _outputs.findAt(
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
    @NonNull final BooleanState current
  ) {
    if (current.getValue()) return;
    
    final Optional<BooleanState> next = _inputs.findNext(current);

    if (!next.isPresent()) {
      begin(current);
    } else if (next.get().getValue()) {
      begin(current, (ActivationState) _outputs.findWithCorrelation("start", next.get().getReference(),
          getSensor().getReference()
      ).get(0));
    } else {
      create(current, next.get());
    }
  }

  private void onInnerInput (
    @NonNull final ActivationState previous, 
    @NonNull final BooleanState current
  ) {
    if (current.getValue()) return;

    final State                  endState  = previous.getCorrelation("end").get();
    final Optional<BooleanState> nextState = _inputs.findNext(current);
    
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
    @NonNull final StateWasMutatedEvent event
  ) {
    super.stateWasMutated(event);
    
    if (Objects.equals(event.getNewValue().getSensor(), getInputSensor())) {
      inputStateWasMutated((BooleanState) event.getOldValue(), (BooleanState) event.getNewValue()
      );
    }
  }
  
  public void inputStateWasMutated (
    @NonNull final BooleanState previous,
    @NonNull final BooleanState next
  ) {
    final Optional<ActivationState> correlated = _outputs.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"), 
      next.getReference(), getSensor().getReference()
    );
    
    if (correlated.isPresent() == false) {
      inputStateWasCreated(next);
    } else if (Objects.equals(next.getEmissionDate(), previous.getEmissionDate()) == false &&
      next.getValue() == previous.getValue()
    ) {
      onBoundaryLocationChange(correlated.get(), next);
    } else if (next.getValue() != previous.getValue()) {
      onBoundaryTypeChange(correlated.get(), next);     
    }
  }

  private void onBoundaryLocationChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    if (changed.equals(correlated.getCorrelation("start"))) {
      onStartBoundaryLocationChange(correlated, changed);
    } else {
      onEndBoundaryLocationChange(correlated, changed);
    }
  }

  private void onEndBoundaryLocationChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = _inputs.findNext(
      correlated.getEnd(), 
      ApplicationEntityReference.of(changed.getSensor())
    ).orElse(null);
    
    final BooleanState previous = _inputs.findPrevious(
      correlated.getEnd(), 
      ApplicationEntityReference.of(changed.getSensor())
    ).orElse(null);
    
    if (Objects.equals(next, changed) || Objects.equals(previous, changed)) {
      finish(correlated, changed);
    } else  {
      resolveHardEndMutation(correlated, changed, next);
    }
  }

  private void onStartBoundaryLocationChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = (BooleanState) _inputs.findNext(
      correlated.getStart(),
      changed.getSensor().getReference()
    ).orElse(null);

    final BooleanState previous = (BooleanState) _inputs.findPrevious(
      correlated.getStart(),
      changed.getSensor().getReference()
    ).orElse(null);
    
    if (Objects.equals(next, changed) || Objects.equals(previous, changed)) {
      begin(changed, correlated);
    } else  {
      resolveHardStartMutation(correlated, changed, next);
    }
  }

  private void onBoundaryTypeChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {    
    if (Objects.equals(changed, correlated.getCorrelation("start"))) {
      onStartBoundaryTypeChange(correlated, changed);
    } else {
      onEndBoundaryTypeChange(correlated, changed);
    }
  }

  private void onEndBoundaryTypeChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = _inputs.findNext((BooleanState) correlated.getCorrelation("end").get()).orElse(null);
    resolveHardEndMutation(correlated, changed, next);
  }

  private void onStartBoundaryTypeChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = (BooleanState) _inputs.findNext(
      correlated.getStart(),
      changed.getSensor().getReference()
    ).orElse(null);
    resolveHardStartMutation(correlated, changed, next);
  }

  private void resolveHardStartMutation (
    @NonNull final ActivationState correlated,
    @NonNull final BooleanState changed,
    @Nullable final BooleanState next
  ) {
    if (next != null && next.getValue()) {
      begin(next, correlated);
    } else {
      delete(correlated);
    }
    
    inputStateWasCreated(changed);
  }


  private void resolveHardEndMutation (
    @NonNull final ActivationState correlated,
    @NonNull final BooleanState changed,
    @Nullable final BooleanState next
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
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);
    
    if (event.getState().getState().is(BooleanState.class)) {
      final BooleanState state = _inputs.find(event.getState().getState().as(BooleanState.class)).get();

      if (state.getSensor().equals(getInputSensor())) {
        inputStateWillBeDeleted(state);
      }
    }
  }

  public void inputStateWillBeDeleted (
    @NonNull final BooleanState state
  ) {
    final Optional<ActivationState> correlated = _outputs.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"),
      ApplicationEntityReference.of(state), 
      ApplicationEntityReference.of(getSensor())
    );
    
    if (correlated.isPresent()) {
      onBoundaryDeletion(correlated.get(), state);
    }
  }

  private void onBoundaryDeletion (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState state
  ) {
    final BooleanState next = _inputs.findNext(state).orElse(null);

    if (Objects.equals(correlated.getCorrelation("end").get(), state)) {
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
    @NonNull final ActivationState left, 
    @NonNull final ActivationState right
  ) {
    final State end = right.getCorrelation("end").get();
    delete(right);
    finish(left, end);
  }

  private void delete (@NonNull final ActivationState right) {
    final StateDeletionSchema deletion = new StateDeletionSchema(right);
    _manager.execute(deletion);
  }

  private void finish (
    @NonNull final ActivationState current, 
    @Nullable final State next
  ) {
    final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
    
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

  private ActivationState begin (@NonNull final State start) {
    return create(start, null);
  }
  
  private ActivationState create (
    @NonNull final State start, 
    @Nullable final State end
  ) {
    final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
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
    @NonNull final BooleanState current, 
    @NonNull final ActivationState state
  ) {
    final ActivationStateMutationSchema schema = new ActivationStateMutationSchema();
    schema.setState(state.getReference());
    schema.setStart(current.getEmissionDate());
    schema.setEmittionDate(current.getEmissionDate());
    schema.correlate("start", current);
    
    _manager.execute(schema);
  }
}
