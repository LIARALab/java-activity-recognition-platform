package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.hibernate.cfg.NotYetImplementedException;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanStateCreationSchema;
import org.liara.api.data.entity.state.BooleanStateMutationSchema;
import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.entity.state.NumericStateSnapshot;
import org.liara.api.data.entity.state.StateDeletionSchema;
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

@UseSensorConfigurationOfType(CeilToUpDownConvertionSensorConfiguration.class)
@EmitStateOfType(BooleanState.class)
@Component
@Scope("prototype")
public class CeilToUpDownConvertionSensor extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;
  
  @NonNull
  private final CeilToUpDownConvertionSensorData _data;
  
  @Autowired
  public CeilToUpDownConvertionSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final CeilToUpDownConvertionSensorData data
  ) {
    super();
    _schemaManager = schemaManager;
    _data = data;
  }
  
  public CeilToUpDownConvertionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      CeilToUpDownConvertionSensorConfiguration.class
    );
  }
  
  public Long getInputSensor () {
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
    
    final List<NumericState> states = _data.fetchAll(getInputSensor());
    
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
    
    if (event.getState().getSensor() == getInputSensor()) {
      inputStateWasCreated(
        NumericState.class.cast(event.getState().getModel())
      );
    }
  }

  private void inputStateWasCreated (
    @NonNull final NumericState current
  ) {
    final NumericState previous = _data.fetchPrevious(current);
    final NumericState next = _data.fetchNext(current);
    
    discover(previous, current);
    correct(current, next);
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateWasMutatedEvent event
  ) {
   super.stateWasMutated(event);
   
   if (event.getState().getSensor() == getInputSensor()) {
     inputStateWasMutated(
       NumericStateSnapshot.class.cast(event.getState()),
       NumericState.class.cast(event.getState().getModel())
     );
   }
  }

  private void inputStateWasMutated (
    @NonNull final NumericStateSnapshot old,
    @NonNull final NumericState current
  ) {
    if (!Objects.equals(current.getNumber(), old.getNumber())) {
      correct(_data.fetchPrevious(current), current);
      correct(current, _data.fetchNext(current));
    }
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  ) {
    super.stateWillBeDeleted(event);
   
    if (_data.getState(event.getState().getIdentifier()).getSensorIdentifier() == getInputSensor()) {
      inputStateWillBeDeleted(
        NumericState.class.cast(
          _data.getState(event.getState().getIdentifier())
        )
      );
    }
  }
  
  private void inputStateWillBeDeleted (
    @NonNull final NumericState toDelete
  ) {
    final NumericState previous = _data.fetchPrevious(toDelete);
    final NumericState next = _data.fetchNext(toDelete);
    
    final BooleanState related = _data.fetchCurrentCorrelation(toDelete);
    
    if (related != null) delete(related);
    if (next != null) correct(previous, next);
  }

  private void correct (
    @NonNull final NumericState current, 
    @Nullable final NumericState next
  ) {
    if (next == null) return;
    
    final BooleanState correlated = _data.fetchCurrentCorrelation(next);
    
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
    deletion.setIdentifier(correlated);
    _schemaManager.execute(deletion);
  }

  private void correctDown (
    @NonNull final NumericState current, 
    @NonNull final NumericState next, 
    @NonNull final BooleanState correlated
  ) {
    final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema();
    mutation.setIdentifier(correlated.getIdentifier());
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
    mutation.setIdentifier(correlated.getIdentifier());
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
      if (current.getNumber().doubleValue() > getCeil()) {
        emitUp(previous, current);
      } else {
        emitDown(previous, current);
      }
    } else {
      double previousValue = previous.getNumber().doubleValue();
      double currentValue = current.getNumber().doubleValue();
      
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
    return isDown(previous.getNumber().doubleValue(), current.getNumber().doubleValue());  
  }
  
  private boolean isDown (final double previous, final double current) {
    return previous >= getCeil() && current < getCeil();
  }
  
  private boolean isUp (
    @NonNull final NumericState previous, 
    @NonNull final NumericState current
  ) {
    return isUp(
      previous.getNumber().doubleValue(), 
      current.getNumber().doubleValue()
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
    creation.setSensor(getSensor());
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
    creation.setSensor(getSensor());
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
        return current.getEmittionDate();
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
