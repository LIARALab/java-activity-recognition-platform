package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.*;

@Schema(State.class)
@JsonDeserialize(using = StateMutationSchemaDeserializer.class)
public class StateMutationSchema implements ApplicationSchema
{
  @Nullable
  private ApplicationEntityReference<? extends State> _state;

  @Nullable
  private ZonedDateTime _emittionDate;
  
  @NonNull
  private final Map<@NonNull String, @NonNull ApplicationEntityReference<State>> _correlations;
  
  @NonNull
  private final Set<@NonNull String> _decorrelations;

  public StateMutationSchema () {
    _state = null;
    _emittionDate = null;
    _correlations = new HashMap<>();
    _decorrelations = new HashSet<>();
  }

  public StateMutationSchema (@NonNull final StateMutationSchema toCopy) {
    _state = toCopy.getState();
    _emittionDate = toCopy.getEmittionDate();
    _correlations = new HashMap<>(toCopy.getCorrelations());
    _decorrelations = new HashSet<>(toCopy.getDecorrelations());
  }

  public void clear () {
    _state = null;
    _emittionDate = null;
    _correlations.clear();
    _decorrelations.clear();
  }
  
  @Required
  @ValidApplicationEntityReference
  public @Nullable ApplicationEntityReference<? extends State> getState () {
    return _state;
  }
  
  public void setState (@Nullable final ApplicationEntityReference<? extends State> state) {
    _state = state;
  }

  public @Nullable ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }

  public void setEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }
  
  public void decorrelate (@NonNull final String label) {
    _correlations.remove(label);
    
    _decorrelations.add(label);
  }
  
  public void correlate (
    @NonNull final String label, 
    @NonNull final Long state
  ) {
    _decorrelations.remove(label);
    
    _correlations.put(label, ApplicationEntityReference.of(State.class, state));
  }
  
  public void correlate (
    @NonNull final String label, 
    @NonNull final State state
  ) {
    correlate(label, state.getIdentifier());
  }

  public @NonNull Set<@NonNull String> getDecorrelations () {
    return Collections.unmodifiableSet(_decorrelations);
  }

  public void setDecorrelations (@Nullable final Set<@NonNull String> decorrelations) {
    _decorrelations.clear();
    if (decorrelations != null) decorrelations.forEach(this::decorrelate);
  }

  public @NonNull Map<@NonNull String, @NonNull ApplicationEntityReference<State>> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }

  public @NonNull ApplicationEntityReference<State> getCorrelation (@NonNull final String name) {
    return _correlations.get(name);
  }
  
  @ValidApplicationEntityReference
  @Required
  public @NonNull Set<@NonNull ApplicationEntityReference<State>> getCorrelated () {
    return Collections.unmodifiableSet(new HashSet<>(_correlations.values()));
  }

  public void apply (
    @NonNull final State state,
    @NonNull final EntityManager manager
  )
  {
    if (_emittionDate != null) state.setEmissionDate(_emittionDate);
    
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
    final State state = getState().resolve(manager);
    apply(state, manager);
    return state;
  }
}
