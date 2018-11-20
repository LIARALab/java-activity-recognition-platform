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

  private final SapaRepositories.@NonNull Numeric _input;

  private final SapaRepositories.@NonNull Boolean _output;

  @NonNull
  private final CorrelationRepository _correlations;
  
  @Autowired
  public CeilToUpDownConvertionSensor (
    @NonNull final ApplicationEventPublisher eventPublisher,
    final SapaRepositories.@NonNull Numeric input,
    final SapaRepositories.@NonNull Boolean output,
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

    @NonNull final List<ValueState.@NonNull Numeric> states = _input.find(getInputSensor(), Cursor.ALL);
    
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
      inputStateWasCreated((ValueState.Numeric) event.getState());
    }
  }

  private void inputStateWasCreated (
    final ValueState.@NonNull Numeric current
  ) {
    final ValueState.@Nullable Numeric previous = _input.findPrevious(current).orElse(null);
    final ValueState.@Nullable Numeric next     = _input.findNext(current).orElse(null);
    
    discover(previous, current);
    correct(current, next);
  }

  @Override
  public void stateWasMutated (
    final StateEvent.@NonNull WasMutated event
  ) {
   super.stateWasMutated(event);

    if (Objects.equals(event.getNewValue().getSensorIdentifier(), getInputSensor())) {
      inputStateWasMutated((ValueState.Numeric) event.getOldValue(), (ValueState.Numeric) event.getNewValue()
     );
   }
  }

  private void inputStateWasMutated (
    final ValueState.@NonNull Numeric old, final ValueState.@NonNull Numeric current
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
      inputStateWillBeDeleted((ValueState.Numeric) event.getState());
    }
  }
  
  private void inputStateWillBeDeleted (
    final ValueState.@NonNull Numeric toDelete
  ) {
    final ValueState.@Nullable Numeric previous = _input.findPrevious(toDelete).orElse(null);
    final ValueState.@Nullable Numeric next     = _input.findNext(toDelete).orElse(null);

    @Nullable final ValueState<Boolean> related = findResultCorrelatedWith(toDelete);
    
    if (related != null) delete(related);
    if (next != null) correct(previous, next);
  }

  private @Nullable ValueState<Boolean> findResultCorrelatedWith (final ValueState.@NonNull Numeric toDelete) {
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
    final ValueState.@NonNull Numeric current, final ValueState.@Nullable Numeric next
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
    final ValueState.@NonNull Numeric current, final ValueState.@NonNull Numeric next,
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
    final ValueState.@NonNull Numeric current, final ValueState.@NonNull Numeric next,
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
    final ValueState.@Nullable Numeric previous, final ValueState.@NonNull Numeric current
  ) {
    if (previous == null) {
      if (((Number) current.getValue()).doubleValue() > getCeil()) {
        emitUp(previous, current);
      } else {
        emitDown(previous, current);
      }
    } else {
      double previousValue = ((Number) previous.getValue()).doubleValue();
      double currentValue  = ((Number) current.getValue()).doubleValue();
      
      if (isUp(previousValue, currentValue)) {
        emitUp(previous, current);
      } else if (isDown(previousValue, currentValue)) {
        emitDown(previous, current);
      }
    }
  }
  
  private boolean isDown (
    final ValueState.@NonNull Numeric previous, final ValueState.@NonNull Numeric current
  ) {
    return isDown(((Number) previous.getValue()).doubleValue(), ((Number) current.getValue()).doubleValue());
  }
  
  private boolean isDown (final double previous, final double current) {
    return previous >= getCeil() && current < getCeil();
  }
  
  private boolean isUp (
    final ValueState.@NonNull Numeric previous, final ValueState.@NonNull Numeric current
  ) {
    return isUp(((Number) previous.getValue()).doubleValue(), ((Number) current.getValue()).doubleValue()
    );  
  }
  
  private boolean isUp (final double previous, final double current) {
    return previous < getCeil() && current >= getCeil();
  }

  private void emitDown (
    final ValueState.@Nullable Numeric previous, final ValueState.@NonNull Numeric current
  )
  { emit(previous, current, false); }

  private void emitUp (
    final ValueState.@Nullable Numeric previous, final ValueState.@NonNull Numeric current
  )
  { emit(previous, current, true);}

  private void emit (
    final ValueState.@Nullable Numeric previous, final ValueState.@NonNull Numeric current, final boolean up
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
    final ValueState.@Nullable Numeric previous, final ValueState.@NonNull Numeric current
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
