package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.cfg.NotYetImplementedException;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.repository.StateRepository;
import org.liara.api.data.schema.BooleanStateCreationSchema;
import org.liara.api.data.schema.BooleanStateMutationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.data.schema.StateDeletionSchema;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@UseSensorConfigurationOfType(CeilToUpDownConvertionSensorConfiguration.class)
@EmitStateOfType(BooleanState.class)
@Component
@Scope("prototype")
public class CeilToUpDownConvertionSensor extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;
  
  @NonNull
  private final StateRepository<NumericState> _data;

  @NonNull
  private final StateRepository<BooleanState> _output;
  
  @Autowired
  public CeilToUpDownConvertionSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final StateRepository<NumericState> data,
    @NonNull final StateRepository<BooleanState> output
  ) {
    super();
    _schemaManager = schemaManager;
    _data = data;
    _output = output;
  }
  
  public CeilToUpDownConvertionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      CeilToUpDownConvertionSensorConfiguration.class
    );
  }
  
  public ApplicationEntityReference<Sensor> getInputSensor () {
    return getConfiguration().getInputSensor();
  }
  
  public InterpolationType getInterpolationType () {
    return getConfiguration().getInterpolationType();
  }
  
  public double getCeil () {
    return getConfiguration().getCeil();
  }
  
  public Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);

    final List<NumericState> states = _data.findAll(getInputSensor());
    
    if (states.size() > 0) {
      discover(null, states.get(0));
    }
    
    for (int index = 1; index < states.size(); ++index) {
      discover(states.get(index - 1), states.get(index));
    }
  }

  @Override
  public void stateWasCreated (
    @NonNull final StateWasCreatedEvent event
  ) {
    super.stateWasCreated(event);

    if (event.getState().getSensor().equals(getInputSensor())) {
      inputStateWasCreated((NumericState) event.getState()
      );
    }
  }

  private void inputStateWasCreated (
    @NonNull final NumericState current
  ) {
    final NumericState previous = _data.findPrevious(current).orElse(null);
    final NumericState next     = _data.findNext(current).orElse(null);
    
    discover(previous, current);
    correct(current, next);
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateWasMutatedEvent event
  ) {
   super.stateWasMutated(event);

    if (event.getNewValue().getSensor().equals(getInputSensor())) {
     inputStateWasMutated((NumericState) event.getOldValue(), (NumericState) event.getNewValue()
     );
   }
  }

  private void inputStateWasMutated (
    @NonNull final NumericState old,
    @NonNull final NumericState current
  ) {
    if (!Objects.equals(current.getValue(), old.getValue())) {
      correct(_data.findPrevious(current).orElse(null), current);
      correct(current, _data.findNext(current).orElse(null));
    }
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);

    if (_data.getAt(event.getState().getState().as(NumericState.class)).getSensor().equals(getInputSensor())) {
      inputStateWillBeDeleted(_data.getAt(event.getState().getState().as(NumericState.class))
      );
    }
  }
  
  private void inputStateWillBeDeleted (
    @NonNull final NumericState toDelete
  ) {
    final NumericState previous = _data.findPrevious(toDelete).orElse(null);
    final NumericState next     = _data.findNext(toDelete).orElse(null);

    final BooleanState related = _output.findFirstWithCorrelation("current", toDelete.getReference(), getInputSensor())
                                        .orElse(null);
    
    if (related != null) delete(related);
    if (next != null) correct(previous, next);
  }

  private void correct (
    @NonNull final NumericState current, 
    @Nullable final NumericState next
  ) {
    if (next == null) return;

    final BooleanState correlated = _output.findFirstWithCorrelation("current", next.getReference(), getInputSensor())
                                           .orElse(null);
    
    if (correlated == null) {
      discover(current, next);
    } else if (isUp(current, next)) {
      correctUp(current, next, correlated);
    } else if (isDown(current, next)) {
      correctDown(current, next, correlated);
    } else {
      delete(correlated);
    }
  }
  
  private void delete (@NonNull final BooleanState correlated) {
    final StateDeletionSchema deletion = new StateDeletionSchema();
    deletion.setState(correlated);
    _schemaManager.execute(deletion);
  }

  private void correctDown (
    @NonNull final NumericState current, 
    @NonNull final NumericState next, 
    @NonNull final BooleanState correlated
  ) {
    final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema();
    mutation.setState(correlated.getReference());
    mutation.correlate("previous", current);
    mutation.setEmittionDate(interpolate(current, next));
    mutation.setValue(false);
    _schemaManager.execute(mutation);
  }

  private void correctUp (
    @NonNull final NumericState current, 
    @NonNull final NumericState next, 
    @NonNull final BooleanState correlated
  ) {
    final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema();
    mutation.setState(correlated.getReference());
    mutation.correlate("previous", current);
    mutation.setEmittionDate(interpolate(current, next));
    mutation.setValue(true);
    _schemaManager.execute(mutation);
  }

  private void discover (
    @Nullable final NumericState previous, 
    @NonNull final NumericState current
  ) {
    if (previous == null) {
      if (current.getValue().doubleValue() > getCeil()) {
        emitUp(previous, current);
      } else {
        emitDown(previous, current);
      }
    } else {
      double previousValue = previous.getValue().doubleValue();
      double currentValue  = current.getValue().doubleValue();
      
      if (isUp(previousValue, currentValue)) {
        emitUp(previous, current);
      } else if (isDown(previousValue, currentValue)) {
        emitDown(previous, current);
      }
    }
  }
  
  private boolean isDown (
    @NonNull final NumericState previous, 
    @NonNull final NumericState current
  ) {
    return isDown(previous.getValue().doubleValue(), current.getValue().doubleValue());
  }
  
  private boolean isDown (final double previous, final double current) {
    return previous >= getCeil() && current < getCeil();
  }
  
  private boolean isUp (
    @NonNull final NumericState previous, 
    @NonNull final NumericState current
  ) {
    return isUp(previous.getValue().doubleValue(), current.getValue().doubleValue()
    );  
  }
  
  private boolean isUp (final double previous, final double current) {
    return previous < getCeil() && current >= getCeil();
  }

  private void emitDown (
    @Nullable final NumericState previous, 
    @NonNull final NumericState current
  ) {
    final BooleanStateCreationSchema creation = new BooleanStateCreationSchema();
    creation.setSensor(getSensor().getReference());
    creation.setEmittionDate(interpolate(previous, current));
    creation.setValue(false);
    if (previous != null) creation.correlate("previous", previous);
    creation.correlate("current", current);
    
    _schemaManager.execute(creation);
  }

  private void emitUp (
    @Nullable final NumericState previous, 
    @NonNull final NumericState current
  ) {
    final BooleanStateCreationSchema creation = new BooleanStateCreationSchema();
    creation.setSensor(getSensor().getReference());
    creation.setEmittionDate(interpolate(previous, current));
    creation.setValue(true);
    if (previous != null) creation.correlate("previous", previous);
    creation.correlate("current", current);
    
    _schemaManager.execute(creation);
  }
  
  private ZonedDateTime interpolate (
    @Nullable final NumericState previous, 
    @NonNull final NumericState current
  ) {
    switch (getInterpolationType()) {
      case NONE:
        return current.getEmissionDate();
      default :
        throw new NotYetImplementedException(
          String.join(
            "",
            "The interpolation type : ", String.valueOf(getInterpolationType()), " is",
            " not implemented yet."
          )
        );
    }
  }
}
