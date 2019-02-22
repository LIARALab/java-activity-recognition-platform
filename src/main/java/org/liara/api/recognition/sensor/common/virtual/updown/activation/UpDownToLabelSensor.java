package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.BooleanStateRepository;
import org.liara.api.data.repository.LabelRepository;
import org.liara.api.data.repository.NodeRepository;
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
  private final SchemaManager _manager;
  
  @NonNull
  private final LabelRepository _outputs;
  
  @NonNull
  private final BooleanStateRepository _inputs;
  
  @NonNull
  private final NodeRepository _nodes;
  
  @Autowired
  public UpDownToLabelSensor (
    @NonNull final SchemaManager manager,
    @NonNull final BooleanStateRepository inputs, @NonNull final LabelRepository outputs,
    @NonNull final NodeRepository nodes
  ) {
    _manager = manager;
    _inputs = inputs;
    _outputs = outputs;
    _nodes = nodes;
  }

  public UpDownToLabelSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(UpDownToLabelSensorConfiguration.class
    );
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  public ApplicationEntityReference<Sensor> getInputSensor () {
    return getConfiguration().getInputSensor();
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);
    
    final List<BooleanState> initializationStates = _inputs.findAll(getInputSensor());
    LabelState               current              = null;
    
    _manager.flush();
    _manager.clear();
    
    for (int index = 0; index < initializationStates.size(); ++index) {
      current = initialize(current, initializationStates.get(index));
    }
  }

  private LabelState initialize (
    @Nullable final LabelState current, 
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
    
    if (
      Objects.equals(
        event.getState().getSensor(), 
        getInputSensor()
      )
    ) {
      inputStateWasCreated((BooleanState) event.getState().getModel());
    }
  }

  public void inputStateWasCreated (@NonNull final BooleanState current) {
    final Optional<LabelState> previous = _outputs.findAt(
      current.getEmissionDate(),
      getSensor().getReference()
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
        _outputs.findWithCorrelation(
          "start", next.get().getReference(), 
          getSensor().getReference()
        ).get(0)
      );
    } else {
      create(current, next.get());
    }
  }

  private void onInnerInput (
    @NonNull final LabelState previous,
    @NonNull final BooleanState current
  ) {
    if (current.getValue()) return;

    final State                  endState  = previous.getCorrelation("end");
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
      inputStateWasMutated((BooleanStateSnapshot) event.getOldValue(), (BooleanState) event.getNewValue().getModel()
      );
    }
  }
  
  public void inputStateWasMutated (
    @NonNull final BooleanStateSnapshot previous,
    @NonNull final BooleanState next
  ) {
    final Optional<LabelState> correlated = _outputs.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"), 
      next.getReference(), getSensor().getReference()
    );

    if (!correlated.isPresent()) {
      inputStateWasCreated(next);
    } else if (
             !Objects.equals(next.getEmissionDate(), previous.getEmittionDate()) &&
      next.getValue() == previous.getValue()
    ) {
      onBoundaryLocationChange(correlated.get(), next);
    } else if (next.getValue() != previous.getValue()) {
      onBoundaryTypeChange(correlated.get(), next);     
    }
  }

  private void onBoundaryLocationChange (
    @NonNull final LabelState correlated,
    @NonNull final BooleanState changed
  ) {
    if (changed.equals(correlated.getCorrelation("start"))) {
      onStartBoundaryLocationChange(correlated, changed);
    } else {
      onEndBoundaryLocationChange(correlated, changed);
    }
  }

  private void onEndBoundaryLocationChange (
    @NonNull final LabelState correlated,
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
    @NonNull final LabelState correlated,
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = _inputs.findNext(
      correlated.getStart(),
      changed.getSensor().getReference()
    ).orElse(null);
   
    final BooleanState previous = _inputs.findPrevious(
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
    @NonNull final LabelState correlated,
    @NonNull final BooleanState changed
  ) {    
    if (Objects.equals(changed, correlated.getCorrelation("start"))) {
      onStartBoundaryTypeChange(correlated, changed);
    } else {
      onEndBoundaryTypeChange(correlated, changed);
    }
  }

  private void onEndBoundaryTypeChange (
    @NonNull final LabelState correlated,
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = _inputs.findNext((BooleanState) correlated.getCorrelation("end")).orElse(null);
    resolveHardEndMutation(correlated, changed, next);
  }

  private void onStartBoundaryTypeChange (
    @NonNull final LabelState correlated,
    @NonNull final BooleanState changed
  ) {
    final BooleanState next = _inputs.findNext(
      correlated.getStart(), 
      changed.getSensor().getReference()
    ).orElse(null);
    resolveHardStartMutation(correlated, changed, next);
  }

  private void resolveHardStartMutation (
    @NonNull final LabelState correlated,
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
    @NonNull final LabelState correlated,
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
      
      if (state.getSensorIdentifier() == getInputSensor().getIdentifier()) {
        inputStateWillBeDeleted(state);
      }
    }
  }

  public void inputStateWillBeDeleted (
    @NonNull final BooleanState state
  ) {
    final Optional<LabelState> correlated = _outputs.findFirstWithAnyCorrelation(
      Arrays.asList("start", "end"),
      ApplicationEntityReference.of(state), 
      ApplicationEntityReference.of(getSensor())
    );
    
    if (correlated.isPresent()) {
      onBoundaryDeletion(correlated.get(), state);
    }
  }

  private void onBoundaryDeletion (
    @NonNull final LabelState correlated,
    @NonNull final BooleanState state
  ) {
    final BooleanState next = _inputs.findNext(state).orElse(null);

    if (Objects.equals(correlated.getCorrelation("end"), state)) {
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
    final State end = right.getCorrelation("end");
    delete(right);
    finish(left, end);
  }

  private void delete (@NonNull final LabelState right) {
    final StateDeletionSchema deletion = new StateDeletionSchema(right);
    _manager.execute(deletion);
  }

  private void finish (
    @NonNull final LabelState current,
    @Nullable final State next
  ) {
    final LabelStateMutationSchema mutation = new LabelStateMutationSchema();
    
    if (next == null) {
      mutation.setEnd(null);
      mutation.decorrelate("end");
    } else {
      mutation.setEnd(next.getEmissionDate());
      mutation.correlate("end", next);
    }
    
    mutation.setState(current.getIdentifier());
    
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
    creation.setEmissionDate(start.getEmissionDate());
    creation.setTag(getConfiguration().getLabel());
    creation.setSensor(getSensor());
    creation.setStart(start.getEmissionDate());
    creation.correlate("start", start);
    
    if (end != null) {
      creation.setEnd(end.getEmissionDate());
      creation.correlate("end", end);
    }
    
    return _manager.execute(creation);
  }
  
  private void begin (
    @NonNull final BooleanState current, @NonNull final LabelState state
  ) {
    final LabelStateMutationSchema schema = new LabelStateMutationSchema();
    schema.setState(state.getIdentifier());
    schema.setStart(current.getEmissionDate());
    schema.setEmissionDate(current.getEmissionDate());
    schema.correlate("start", current);
    
    _manager.execute(schema);
  }
}
