package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.ApplicationEntitySnapshot;
import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;

public class StateSnapshot extends ApplicationEntitySnapshot
{
  @NonNull
  private final ApplicationEntityReference<Sensor> _sensor;
  
  @NonNull
  private final ZonedDateTime _emittionDate;
  
  @NonNull
  private final Map<String, ApplicationEntityReference<State>> _correlations = new HashMap<>();
  
  public StateSnapshot (@NonNull final StateSnapshot toCopy) {
    super(toCopy);
    
    _sensor = toCopy.getSensor();
    _emittionDate = toCopy.getEmittionDate();
    for (final Map.Entry<String, ApplicationEntityReference<State>> correlation : toCopy.getCorrelations().entrySet()) {
      _correlations.put(correlation.getKey(), correlation.getValue());
    }
  }

  public StateSnapshot (@NonNull final State model) {
    super(model);
    
    _sensor = ApplicationEntityReference.of(Sensor.class, model.getSensorIdentifier());
    _emittionDate = model.getEmittionDate();
    for (final Map.Entry<String, State> correlation : model.getCorrelations()) {
      _correlations.put(correlation.getKey(), correlation.getValue().getReference().as(State.class));
    }
  }
  
  public ApplicationEntityReference<Sensor> getSensor () {
    return _sensor;
  }
  
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  public Map<String, ApplicationEntityReference<State>> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }
  
  public ApplicationEntityReference<State> getCorrelation (@NonNull final String name) {
    return _correlations.get(name);
  }
  
  public boolean hasCorrelation (@NonNull final String name) {
    return _correlations.containsKey(name);
  }
  
  public Iterable<Map.Entry<String, ApplicationEntityReference<State>>> correlations () {
    return Collections.unmodifiableSet(_correlations.entrySet());
  }
  
  @Override
  public StateSnapshot clone () {
    return new StateSnapshot(this);
  }
  
  @Override
  public State getModel () {
    return (State) super.getModel();
  }
}
