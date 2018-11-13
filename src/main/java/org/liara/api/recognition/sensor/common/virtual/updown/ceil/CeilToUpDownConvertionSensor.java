package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.cfg.NotYetImplementedException;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@UseSensorConfigurationOfType(CeilToUpDownConvertionSensorConfiguration.class)
@EmitStateOfType(ValueState.Boolean.class)
@Component
@Scope("prototype")
public class CeilToUpDownConvertionSensor extends AbstractVirtualSensorHandler
{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @NonNull
  private final ValueStateRepository<? extends Number> _input;

  @NonNull
  private final ValueStateRepository<Boolean> _output;
  
  @Autowired
  public CeilToUpDownConvertionSensor (
    @NonNull final ApplicationEventPublisher eventPublisher,
    @NonNull final ValueStateRepository<? extends Number> input,
    @NonNull final ValueStateRepository<Boolean> output
  ) {
    super();
    _eventPublisher = eventPublisher;
    _input = input;
    _output = output;
  }

  public @NonNull CeilToUpDownConvertionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      CeilToUpDownConvertionSensorConfiguration.class
    );
  }

  public @NonNull ApplicationEntityReference<? extends Sensor> getInputSensor () {
    return getConfiguration().getInputSensor();
  }

  public @NonNull InterpolationType getInterpolationType () {
    return getConfiguration().getInterpolationType();
  }
  
  public double getCeil () {
    return getConfiguration().getCeil();
  }

  public @NonNull Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  ) {
    super.initialize(runner);

    @NonNull final List<@NonNull ValueState<? extends Number>> states = _input.find(getInputSensor(), Cursor.ALL);
    
    if (states.size() > 0) {
      discover(null, states.get(0));
    }
    
    for (int index = 1; index < states.size(); ++index) {
      discover(states.get(index - 1), states.get(index));
    }
  }

  @Override
  public void stateWasCreated (
    final StateEvent.@NonNull WasCreated event
  ) {
    super.stateWasCreated(event);

    if (Objects.equals(event.getState().getSensorIdentifier(), getInputSensor())) {
      inputStateWasCreated((ValueState<? extends Number>) event.getState());
    }
  }

  private void inputStateWasCreated (
    @NonNull final ValueState<? extends Number> current
  ) {
    @Nullable final ValueState<? extends Number> previous = _input.findPrevious(current).orElse(null);
    @Nullable final ValueState<? extends Number> next     = _input.findNext(current).orElse(null);
    
    discover(previous, current);
    correct(current, next);
  }

  @Override
  public void stateWasMutated (
    final StateEvent.@NonNull WasMutated event
  ) {
   super.stateWasMutated(event);

    if (Objects.equals(event.getNewValue().getSensorIdentifier(), getInputSensor())) {
      inputStateWasMutated((ValueState<? extends Number>) event.getOldValue(),
                           (ValueState<? extends Number>) event.getNewValue()
     );
   }
  }

  private void inputStateWasMutated (
    @NonNull final ValueState<? extends Number> old, @NonNull final ValueState<? extends Number> current
  ) {
    if (!Objects.equals(current.getValue(), old.getValue())) {
      correct(_input.findPrevious(current).orElse(null), current);
      correct(current, _input.findNext(current).orElse(null));
    }
  }

  @Override
  public void stateWillBeDeleted (
    final StateEvent.@NonNull WillBeDeleted event
  ) {
    super.stateWillBeDeleted(event);

    if (Objects.equals(event.getState().getSensorIdentifier(), getInputSensor())) {
      inputStateWillBeDeleted((ValueState<? extends Number>) event.getState());
    }
  }
  
  private void inputStateWillBeDeleted (
    @NonNull final ValueState<? extends Number> toDelete
  ) {
    @Nullable final ValueState<? extends Number> previous = _input.findPrevious(toDelete).orElse(null);
    @Nullable final ValueState<? extends Number> next     = _input.findNext(toDelete).orElse(null);

    @Nullable final ValueState<Boolean> related = _output.findFirstWithCorrelation(
      "current",
      toDelete.getReference(),
      getInputSensor()
    )
                                        .orElse(null);
    
    if (related != null) delete(related);
    if (next != null) correct(previous, next);
  }

  private void correct (
    @NonNull final ValueState<? extends Number> current, @Nullable final ValueState<? extends Number> next
  ) {
    if (next == null) return;

    @Nullable final ValueState<Boolean> correlated = _output.findFirstWithCorrelation("current",
                                                                                      next.getReference(),
                                                                                      getInputSensor()
    )
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

  private void delete (@NonNull final ValueState<Boolean> correlated) {
    _eventPublisher.publishEvent(new ApplicationEntityEvent.Delete(correlated));
  }

  private void correctDown (
    @NonNull final ValueState<? extends Number> current,
    @NonNull final ValueState<? extends Number> next,
    @NonNull final ValueState<Boolean> correlated
  ) {
    @NonNull final ValueState<Boolean> mutation = Duplicator.duplicate(correlated);
    mutation.correlate("previous", current);
    mutation.setEmissionDate(interpolate(current, next));
    mutation.setValue(false);
    _schemaManager.execute(mutation);
  }

  private void correctUp (
    @NonNull final ValueState<? extends Number> current,
    @NonNull final ValueState<? extends Number> next,
    @NonNull final ValueState<Boolean> correlated
  ) {
    final ValueState<Boolean> MutationSchema mutation = new ValueState<Boolean> MutationSchema();
    mutation.setState(correlated.getReference());
    mutation.correlate("previous", current);
    mutation.setEmittionDate(interpolate(current, next));
    mutation.setValue(true);
    _schemaManager.execute(mutation);
  }

  private void discover (
    @Nullable final ValueState<? extends Number> previous, @NonNull final ValueState<? extends Number> current
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
    @NonNull final ValueState<? extends Number> previous, @NonNull final ValueState<? extends Number> current
  ) {
    return isDown(previous.getValue().doubleValue(), current.getValue().doubleValue());
  }
  
  private boolean isDown (final double previous, final double current) {
    return previous >= getCeil() && current < getCeil();
  }
  
  private boolean isUp (
    @NonNull final ValueState<? extends Number> previous, @NonNull final ValueState<? extends Number> current
  ) {
    return isUp(previous.getValue().doubleValue(), current.getValue().doubleValue()
    );  
  }
  
  private boolean isUp (final double previous, final double current) {
    return previous < getCeil() && current >= getCeil();
  }

  private void emitDown (
    @Nullable final ValueState<? extends Number> previous, @NonNull final ValueState<? extends Number> current
  ) {
    final ValueState<Boolean> CreationSchema creation = new ValueState<Boolean> CreationSchema();
    creation.setSensor(getSensor().getReference());
    creation.setEmittionDate(interpolate(previous, current));
    creation.setValue(false);
    if (previous != null) creation.correlate("previous", previous);
    creation.correlate("current", current);
    
    _schemaManager.execute(creation);
  }

  private void emitUp (
    @Nullable final ValueState<? extends Number> previous, @NonNull final ValueState<? extends Number> current
  ) {
    final ValueState<Boolean> CreationSchema creation = new ValueState<Boolean> CreationSchema();
    creation.setSensor(getSensor().getReference());
    creation.setEmittionDate(interpolate(previous, current));
    creation.setValue(true);
    if (previous != null) creation.correlate("previous", previous);
    creation.correlate("current", current);
    
    _schemaManager.execute(creation);
  }
  
  private ZonedDateTime interpolate (
    @Nullable final ValueState<? extends Number> previous, @NonNull final ValueState<? extends Number> current
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
