package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(State.class)
@JsonDeserialize(using = StateMutationSchemaDeserializer.class)
public class StateMutationSchema
{
  @Nullable
  private Long _identifier = null;
  
  @Nullable
  private ZonedDateTime _emittionDate = null;
  
  @NonNull
  private final Map<String, Long> _correlations = new HashMap<>();
  
  @NonNull
  private final Set<String> _decorrelations = new HashSet<>();
  
  public void clear () {
    _identifier = null;
    _emittionDate = null;
    _correlations.clear();
    _decorrelations.clear();
  }
  
  @Required
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  public Long getIdentifier () {
    return _identifier;
  }
  
  public void setIdentifier (@Nullable final State state) {
    if (state == null) {
      _identifier = null;
    } else {
      _identifier = state.getIdentifier();
    }
  }
  
  @JsonSetter
  public void setIdentifier (@Nullable final Long identifier) {
    _identifier = identifier;
  }
  
  public void setIdentifier (@NonNull final Optional<Long> identifier) {
    _identifier = identifier.orElse(null);
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
    
    _correlations.put(label, state);
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
  
  public Iterable<String> decorrelations () {
    return Collections.unmodifiableSet(_decorrelations);
  }
  
  public Map<String, Long> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }
  
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  public Set<Long> getCorrelated () {
    return Collections.unmodifiableSet(new HashSet<>(_correlations.values()));
  }
  
  public Iterable<Map.Entry<String, Long>> correlations () {
    return Collections.unmodifiableSet(_correlations.entrySet());
  }
  
  protected void apply (@NonNull final State state) {
    if (_emittionDate != null) state.setEmittionDate(_emittionDate);
    
    for (final String decorrelation : _decorrelations) {
      state.decorrelate(decorrelation);
    }
    
    for (final Map.Entry<String, Long> correlation : _correlations.entrySet()) {
      state.correlate(
        correlation.getKey(), 
        EntityCollections.STATES.findByIdentifier(correlation.getValue()).get()
      );
    }
  }
  
  public State apply (@NonNull final StateCollection collection) {
    final State state = collection.findByIdentifier(_identifier).get();
    apply(state);
    return state;
  }
}
