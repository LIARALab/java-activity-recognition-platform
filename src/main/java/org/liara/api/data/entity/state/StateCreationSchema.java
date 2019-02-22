package org.liara.api.data.entity.state;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.ApplicationSchema;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.*;

@Schema(State.class)
@JsonDeserialize(using = StateCreationSchemaDeserializer.class)
public class StateCreationSchema implements ApplicationSchema
{
  @Nullable
  private ZonedDateTime _emissionDate = null;
  
  @NonNull
  private ApplicationEntityReference<Sensor> _sensor = ApplicationEntityReference.empty(Sensor.class);
  
  @NonNull
  private final Map<String, ApplicationEntityReference<State>> _correlations = new HashMap<>();
  
  public void clear () {
    _emissionDate = null;
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
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  public ZonedDateTime getEmissionDate () {
    return _emissionDate;
  }
  
  @JsonSetter
  public void setEmissionDate (@Nullable final ZonedDateTime emissionDate) {
    _emissionDate = emissionDate;
  }
  
  public void setEmittionDate (@NonNull final Optional<ZonedDateTime> emittionDate) {
    _emissionDate = emittionDate.orElse(null);
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
