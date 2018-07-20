package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.ApplicationSchema;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(State.class)
@JsonDeserialize(using = StateCreationSchemaDeserializer.class)
public class StateCreationSchema implements ApplicationSchema
{
  @Nullable
  private ZonedDateTime _emittionDate = null;
  
  @NonNull
  private ApplicationEntityReference<Sensor> _sensor = ApplicationEntityReference.empty(Sensor.class);
  
  @NonNull
  private final Map<String, ApplicationEntityReference<State>> _correlations = new HashMap<>();
  
  public void clear () {
    _emittionDate = null;
    _sensor = ApplicationEntityReference.empty(Sensor.class);
    _correlations.clear();
  }
  
  @Required
  @ValidApplicationEntityReference
  public ApplicationEntityReference<Sensor> getSensor () {
    return _sensor;
  }
  
  @JsonSetter
  public void setSensor (@Nullable final Long sensor) {
    _sensor = ApplicationEntityReference.of(Sensor.class, sensor);
  }
  
  public void setSensor (@Nullable final Sensor sensor) {
    _sensor = (sensor == null) ? ApplicationEntityReference.empty(Sensor.class)
                               : ApplicationEntityReference.of(sensor);
  }
  
  @Required
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  @JsonSetter
  public void setEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }
  
  public void setEmittionDate (@NonNull final Optional<ZonedDateTime> emittionDate) {
    _emittionDate = emittionDate.orElse(null);
  }
  
  public void correlate (
    @NonNull final String label, 
    @NonNull final State state
  ) {
    _correlations.put(label, ApplicationEntityReference.of(state));
  }
  
  public void decorrelate (@NonNull final String label) {
    _correlations.remove(label);
  }
  
  public ApplicationEntityReference<State> getCorrelation (@NonNull final String label) {
    return _correlations.get(label);
  }
  
  public Map<String, ApplicationEntityReference<State>> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }
  
  public Iterable<Map.Entry<String, ApplicationEntityReference<State>>> correlations () {
    return Collections.unmodifiableSet(_correlations.entrySet());
  }
  
  @ValidApplicationEntityReference
  @Required
  public Set<ApplicationEntityReference<State>> getCorrelated () {
    return Collections.unmodifiableSet(new HashSet<>(_correlations.values()));
  }
}
