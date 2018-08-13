package org.liara.api.data.entity.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.ApplicationSchema;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.*;

@Schema(State.class)
@JsonDeserialize(using = StateMutationSchemaDeserializer.class)
public class StateMutationSchema implements ApplicationSchema
{
  @NonNull
  private ApplicationEntityReference<? extends State> _state = ApplicationEntityReference.empty(State.class);
  
  @Nullable
  private ZonedDateTime _emittionDate = null;
  
  @NonNull
  private final Map<String, ApplicationEntityReference<State>> _correlations = new HashMap<>();
  
  @NonNull
  private final Set<String> _decorrelations = new HashSet<>();
  
  public void clear () {
    _state = ApplicationEntityReference.empty(State.class);
    _emittionDate = null;
    _correlations.clear();
    _decorrelations.clear();
  }
  
  @Required
  @ValidApplicationEntityReference
  public ApplicationEntityReference<? extends State> getState () {
    return _state;
  }
  
  public <Entity extends State> void setState (@Nullable final Entity state) {
    _state = (state == null) ? ApplicationEntityReference.empty(State.class)
                             : ApplicationEntityReference.of(state);
  }
  
  public void setState (@Nullable final ApplicationEntityReference<? extends State> state) {
    _state = (state == null) ? ApplicationEntityReference.empty(State.class)
                             : state;
  }
  
  @JsonSetter
  public void setState (@Nullable final Long identifier) {
    _state = ApplicationEntityReference.of(State.class, identifier);
  }
  
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
  
  public void decorrelate (@NonNull final String label) {
    if (_correlations.containsKey(label)) {
      _correlations.remove(label);
    }
    
    _decorrelations.add(label);
  }
  
  public void correlate (
    @NonNull final String label, 
    @NonNull final Long state
  ) {
    if (_decorrelations.contains(label)) {
      _decorrelations.remove(label);
    }
    
    _correlations.put(label, ApplicationEntityReference.of(State.class, state));
  }
  
  public void correlate (
    @NonNull final String label, 
    @NonNull final State state
  ) {
    correlate(label, state.getIdentifier());
  }
  
  public Set<String> getDecorrelations () {
    return Collections.unmodifiableSet(_decorrelations);
  }
  
  @JsonIgnore
  public Map<String, Boolean> getDecorrelationsMap () {
    final Map<String, Boolean> result = new HashMap<>();
    for (final String decorrelation : _decorrelations) {
      result.put(decorrelation, true);
    }
    return result;
  }
  
  public Iterable<String> decorrelations () {
    return Collections.unmodifiableSet(_decorrelations);
  }
  
  public Map<String, ApplicationEntityReference<State>> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }
 
  public ApplicationEntityReference<State> getCorrelation (@NonNull final String name) {
    return _correlations.get(name);
  }
  
  @ValidApplicationEntityReference
  @Required
  public Set<ApplicationEntityReference<State>> getCorrelated () {
    return Collections.unmodifiableSet(new HashSet<>(_correlations.values()));
  }
  
  public Iterable<Map.Entry<String, ApplicationEntityReference<State>>> correlations () {
    return Collections.unmodifiableSet(_correlations.entrySet());
  }

  public void apply (
    @NonNull final State state,
    @NonNull final EntityManager manager
  )
  {
    if (_emittionDate != null) state.setEmittionDate(_emittionDate);
    
    for (final String decorrelation : _decorrelations) {
      state.decorrelate(decorrelation);
    }
    
    for (final Map.Entry<String, ApplicationEntityReference<State>> correlation : _correlations.entrySet()) {
      state.correlate(
        correlation.getKey(),
        correlation.getValue()
                   .resolve(manager)
      );
    }
  }

  public State apply (@NonNull final EntityManager manager) {
    final State state = getState().resolve();
    apply(state, manager);
    return state;
  }
}
