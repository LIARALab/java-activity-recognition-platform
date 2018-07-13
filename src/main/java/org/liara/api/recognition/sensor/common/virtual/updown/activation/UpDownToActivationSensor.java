package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivationStateCreationSchema;
import org.liara.api.data.entity.state.ActivationStateMutationSchema;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanStateSnapshot;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateDeletionSchema;
import org.liara.api.data.repository.ActivationsRepository;
import org.liara.api.data.repository.TimeSeriesRepository;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
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
  private final ActivationsRepository _activations;
  
  @NonNull
  private final TimeSeriesRepository<BooleanState> _inputs;
  
  @Autowired
  public UpDownToActivationSensor (
    @NonNull final SchemaManager manager,
    @NonNull final ActivationsRepository activations,
    @NonNull final TimeSeriesRepository<BooleanState> inputs
  ) {
    _manager = manager;
    _activations = activations;
    _inputs = inputs;
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
  
  public ApplicationEntityReference<Node> getActivatedNode () {
    final ApplicationEntityReference<Node> result = getConfiguration().getActivatedNode();
    
    return (result.isNull()) ? ApplicationEntityReference.of(getRunner().getSensor().getNode())
                             : result;
  }
  
  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);
    
    final List<BooleanState> initializationStates = _inputs.findAll(getInputSensor());
    ActivationState current = null;
    
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
    @NonNull final StateWasCreatedEvent event
  ) {
    super.stateWasCreated(event);
    
    if (event.getState().getSensor() == getInputSensor()) {
      inputStateWasCreated(BooleanState.class.cast(
        event.getState().getModel()
      ));
    }
  }

  private void inputStateWasCreated (@NonNull final BooleanState current) {
    final Optional<ActivationState> previous = _activations.at(
      current.getEmittionDate(), 
      ApplicationEntityReference.of(getSensor())
    );
    
    if (previous.isPresent() && previous.get().contains(current)) {
      onInnerInput(previous.get(), current);
    } else {
      onAloneInput(current);
    }
  }

  private void onAloneInput (
    @NonNull final BooleanState current
  ) {
    if (!current.getValue()) return;
    
    final Optional<BooleanState> next = _inputs.findNext(current);
    
    if (!next.isPresent()) {
      begin(current);
    } else if (next.get().getValue()) {
      begin(
        current, 
        _activations.findWithCorrelation(
          "start", 
          ApplicationEntityReference.of(next.get()), 
          ApplicationEntityReference.of(getSensor())
        ).get(0)
      );
    } else {
      create(current, next.get());
    }
  }

  private void onInnerInput (
    @NonNull final ActivationState previous, 
    @NonNull final BooleanState current
  ) {
    if (current.getValue()) return;
    
    final State endState = previous.getEndState();
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
    
    if (event.getState().getSensor() == getInputSensor()) {
      inputStateWasMutated(
        BooleanStateSnapshot.class.cast(
          event.getState()
        ),
        BooleanState.class.cast(
          event.getState().getModel()
        )
      );
    }
  }
  
  private void inputStateWasMutated (
    @NonNull final BooleanStateSnapshot previous,
    @NonNull final BooleanState next
  ) {
    final Optional<ActivationState> correlated = _activations.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"), 
      ApplicationEntityReference.of(next), 
      ApplicationEntityReference.of(getSensor())
    );
    
    if (!correlated.isPresent()) {
      inputStateWasCreated(next);
    } else if (!Objects.equals(next.getEmittionDate(), previous.getEmittionDate())) {
      onBoundaryLocationChange(correlated.get(), next);
    } else if (next.getValue() != previous.getValue()) {
      onBoundaryTypeChange(correlated.get(), next);     
    }
  }

  private void onBoundaryLocationChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    if (changed.equals(correlated.getStartState())) {
      final BooleanState next = _inputs.findNext(
        correlated.getStart(),
        ApplicationEntityReference.of(changed.getSensor())
      ).orElse(null);
      
      if (Objects.equals(next, changed)) {
        if (!next.getValue()) {
          onBoundaryTypeChange(correlated, changed);
        } else {
          begin(changed, correlated);
        }
      } else {
        if (next != null && next.getValue()) {
          begin(next, correlated);
        } else {
          delete(correlated);
        }
        
        inputStateWasCreated(changed);
      }
    } else if (changed.equals(correlated.getEndState())) {
      final BooleanState next = _inputs.findNext(
        correlated.getEnd(), 
        ApplicationEntityReference.of(changed.getSensor())
      ).orElse(null);
      
      if (next != null && next.getValue()) {
        merge(correlated, _activations.findFirstWithAnyCorrelation(
          Arrays.asList("start", "end"),
          ApplicationEntityReference.of(next), 
          ApplicationEntityReference.of(getSensor())
        ).get());
      } else {
        finish(correlated, next);
      }
      
      if (!Objects.equals(next, changed)) {
        inputStateWasCreated(changed);
      }
    }
  }

  private void onBoundaryTypeChange (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = _inputs.findNext(changed).orElse(null);
    
    if (changed.getValue()) {
      if (next.getValue()) {
        merge(correlated, _activations.findFirstWithAnyCorrelation(
          Arrays.asList("start", "end"),
          ApplicationEntityReference.of(next), 
          ApplicationEntityReference.of(getSensor())
        ).get());
      } else {
        finish(correlated, next);
      }
    } else {
      if (next.getValue()) {
        begin(next, correlated);
      } else {
        delete(correlated);
      }
    }
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);
    
    if (event.getState().getState().is(BooleanState.class)) {
      final BooleanState state = _inputs.find(event.getState().getState().as(BooleanState.class)).get();
      
      if (state.getSensorIdentifier() == getInputSensor().getIdentifier()) {
        inputStateWillBeDeleted(state);
      }
    }
  }

  private void inputStateWillBeDeleted (
    @NonNull final BooleanState state
  ) {
    final ActivationState correlated = _activations.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"),
      ApplicationEntityReference.of(state), 
      ApplicationEntityReference.of(getSensor())
    ).get();
    
    if (correlated == null) {
      return;
    } else {
      onBoundaryDeletion(correlated, state);
    }
  }

  private void onBoundaryDeletion (
    @NonNull final ActivationState correlated, 
    @NonNull final BooleanState state
  ) {
    final BooleanState next = _inputs.findNext(state).orElse(null);
    
    if (Objects.equals(correlated.getEndState(), state)) {
      if (next != null && next.getValue()) {
        merge(correlated, _activations.findFirstWithAnyCorrelation(
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
    final State end = right.getEndState();
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
      mutation.setEnd(next.getEmittionDate());
      mutation.correlate("end", next);
    }
    
    mutation.setState(current.getIdentifier());
    
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
    creation.setEmittionDate(start.getEmittionDate());
    creation.setNode(getActivatedNode().resolve());
    creation.setSensor(getSensor());
    creation.setStart(start.getEmittionDate());
    creation.correlate("start", start);
    
    if (end != null) {
      creation.setEnd(end.getEmittionDate());
      creation.correlate("end", end);
    }
    
    return _manager.execute(creation);
  }
  
  private void begin (
    @NonNull final BooleanState current, 
    @NonNull final ActivationState state
  ) {
    final ActivationStateMutationSchema schema = new ActivationStateMutationSchema();
    schema.setState(state.getIdentifier());
    schema.setStart(current.getEmittionDate());
    schema.setEmittionDate(current.getEmittionDate());
    schema.correlate("start", current);
    
    _manager.execute(schema);
  }
}
