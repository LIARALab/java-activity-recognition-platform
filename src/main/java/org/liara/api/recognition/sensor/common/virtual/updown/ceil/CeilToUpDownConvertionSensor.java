package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.cfg.NotYetImplementedException;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.ValueStateRepository;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope("prototype")
public class CeilToUpDownConvertionSensor
  extends AbstractVirtualSensorHandler
  implements ComputedSensorType

{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @NonNull
  private final ValueStateRepository<Number> _input;

  @NonNull
  private final ValueStateRepository<Boolean> _output;

  @NonNull
  private final CorrelationRepository _correlations;
  
  @Autowired
  public CeilToUpDownConvertionSensor (
    @NonNull final ApplicationEventPublisher eventPublisher,
    @NonNull final ValueStateRepository<Number> input,
    @NonNull final ValueStateRepository<Boolean> output,
    @NonNull final CorrelationRepository correlations
  ) {
    super();
    _eventPublisher = eventPublisher;
    _input = input;
    _output = output;
    _correlations = correlations;
  }

  public @NonNull CeilToUpDownConvertionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(
      CeilToUpDownConvertionSensorConfiguration.class
    );
  }

  public @NonNull Long getInputSensor () {
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

    @NonNull final List<@NonNull ValueState<Number>> states = _input.find(getInputSensor(), Cursor.ALL);
    
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
      inputStateWasCreated((ValueState<Number>) event.getState());
    }
  }

  private void inputStateWasCreated (
    @NonNull final ValueState<Number> current
  ) {
    @Nullable final ValueState<Number> previous = _input.findPrevious(current).orElse(null);
    @Nullable final ValueState<Number> next     = _input.findNext(current).orElse(null);
    
    discover(previous, current);
    correct(current, next);
  }

  @Override
  public void stateWasMutated (
    final StateEvent.@NonNull WasMutated event
  ) {
   super.stateWasMutated(event);

    if (Objects.equals(event.getNewValue().getSensorIdentifier(), getInputSensor())) {
      inputStateWasMutated((ValueState<Number>) event.getOldValue(), (ValueState<Number>) event.getNewValue()
     );
   }
  }

  private void inputStateWasMutated (
    @NonNull final ValueState<Number> old, @NonNull final ValueState<Number> current
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
      inputStateWillBeDeleted((ValueState<Number>) event.getState());
    }
  }
  
  private void inputStateWillBeDeleted (
    @NonNull final ValueState<Number> toDelete
  ) {
    @Nullable final ValueState<Number> previous = _input.findPrevious(toDelete).orElse(null);
    @Nullable final ValueState<Number> next     = _input.findNext(toDelete).orElse(null);

    @Nullable final ValueState<Boolean> related = findResultCorrelatedWith(toDelete);
    
    if (related != null) delete(related);
    if (next != null) correct(previous, next);
  }

  private @Nullable ValueState<Boolean> findResultCorrelatedWith (@NonNull final ValueState<Number> toDelete) {
    @NonNull final Optional<Correlation> correlation =
      _correlations.findFirstCorrelationFromSeriesWithNameAndThatEndsBy(
      getSensor().getIdentifier(),
      "current",
      toDelete.getIdentifier()
    );

    if (correlation.isPresent()) {
      return _output.find(correlation.get().getStartStateIdentifier()).orElse(null);
    } else {
      return null;
    }
  }

  private void correct (
    @NonNull final ValueState<Number> current, @Nullable final ValueState<Number> next
  ) {
    if (next == null) return;

    @Nullable final ValueState<Boolean> correlated = findResultCorrelatedWith(next);
    
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

  private @NonNull Correlation getPrevious (@NonNull final ValueState<Boolean> target) {
    return _correlations
             .findCorrelationsWithNameAndThatStartBy("previous",
                                                     target.getIdentifier(),
                                                     Cursor.FIRST
             )
             .get(0);
  }

  private void correctDown (
    @NonNull final ValueState<Number> current, @NonNull final ValueState<Number> next,
    @NonNull final ValueState<Boolean> correlated
  ) {
    @NonNull final ValueState<Boolean> mutation = Duplicator.duplicate(correlated);
    mutation.setEmissionDate(interpolate(current, next));
    mutation.setValue(false);

    @NonNull final Correlation correlation = getPrevious(correlated);
    correlation.setEndStateIdentifier(current.getIdentifier());

    _eventPublisher.publishEvent(new ApplicationEntityEvent.Update(this, mutation, correlation));
  }

  private void correctUp (
    @NonNull final ValueState<Number> current, @NonNull final ValueState<Number> next,
    @NonNull final ValueState<Boolean> correlated
  ) {
    @NonNull final ValueState<Boolean> mutation = Duplicator.duplicate(correlated);
    mutation.setEmissionDate(interpolate(current, next));
    mutation.setValue(true);

    @NonNull final Correlation correlation = getPrevious(correlated);
    correlation.setEndStateIdentifier(current.getIdentifier());

    _eventPublisher.publishEvent(new ApplicationEntityEvent.Update(this, mutation, correlation));
  }

  private void discover (
    @Nullable final ValueState<Number> previous, @NonNull final ValueState<Number> current
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
    @NonNull final ValueState<Number> previous, @NonNull final ValueState<Number> current
  ) {
    return isDown(previous.getValue().doubleValue(), current.getValue().doubleValue());
  }
  
  private boolean isDown (final double previous, final double current) {
    return previous >= getCeil() && current < getCeil();
  }
  
  private boolean isUp (
    @NonNull final ValueState<Number> previous, @NonNull final ValueState<Number> current
  ) {
    return isUp(previous.getValue().doubleValue(), current.getValue().doubleValue()
    );  
  }
  
  private boolean isUp (final double previous, final double current) {
    return previous < getCeil() && current >= getCeil();
  }

  private void emitDown (
    @Nullable final ValueState<Number> previous, @NonNull final ValueState<Number> current
  )
  { emit(previous, current, false); }

  private void emitUp (
    @Nullable final ValueState<Number> previous, @NonNull final ValueState<Number> current
  )
  { emit(previous, current, true);}

  private void emit (
    @Nullable final ValueState<Number> previous, @NonNull final ValueState<Number> current, final boolean up
  ) {
    @NonNull final ValueState<Boolean> result = new ValueState.Boolean();
    result.setSensorIdentifier(getSensor().getIdentifier());
    result.setEmissionDate(interpolate(previous, current));
    result.setValue(up);

    _eventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, result));

    @NonNull final Correlation currentCorrelation = new Correlation();
    currentCorrelation.setStartStateIdentifier(result.getIdentifier());
    currentCorrelation.setName("current");
    currentCorrelation.setEndStateIdentifier(current.getIdentifier());

    _eventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, currentCorrelation));

    if (previous != null) {
      @NonNull final Correlation previousCorrelation = new Correlation();
      previousCorrelation.setStartStateIdentifier(result.getIdentifier());
      previousCorrelation.setName("previous");
      previousCorrelation.setEndStateIdentifier(previous.getIdentifier());

      _eventPublisher.publishEvent(new ApplicationEntityEvent.Create(this, previousCorrelation));
    }
  }

  private @NonNull ZonedDateTime interpolate (
    @Nullable final ValueState<Number> previous, @NonNull final ValueState<Number> current
  ) {
    switch (getInterpolationType()) {
      case NONE:
        return current.getEmissionDate();
      default :
        throw new NotYetImplementedException(
          "The interpolation type : " + String.valueOf(getInterpolationType()) + " is not implemented yet."
        );
    }
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return ValueState.Boolean.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return CeilToUpDownConvertionSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:ceil";
  }
}
