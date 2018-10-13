package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;

import java.time.ZonedDateTime;
import java.util.*;

@Schema(State.class)
@JsonDeserialize(using = StateCreationSchemaDeserializer.class)
public class StateCreationSchema
  extends ApplicationEntityCreationSchema
{
  @Nullable
  private ZonedDateTime _emittionDate;

  @Nullable
  private ApplicationEntityReference<Sensor> _sensor;

  @NonNull
  private final Map<@NonNull String, @NonNull ApplicationEntityReference<State>> _correlations;

  public StateCreationSchema () {
    _emittionDate = null;
    _sensor = null;
    _correlations = new HashMap<>();
  }

  public StateCreationSchema (@NonNull final StateCreationSchema toCopy) {
    _emittionDate = toCopy.getEmittionDate();
    _sensor = toCopy.getSensor();
    _correlations = new HashMap<>(toCopy.getCorrelations());
  }

  public void clear () {
    _emittionDate = null;
    _sensor = null;
    _correlations.clear();
  }

  @Required
  @ValidApplicationEntityReference
  public @Nullable ApplicationEntityReference<Sensor> getSensor () {
    return _sensor;
  }

  public void setSensor (@Nullable final ApplicationEntityReference<Sensor> sensor) {
    _sensor = sensor;
  }

  @Required
  public @Nullable ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }

  public void setEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }

  public void correlate (
    @NonNull final String label, @NonNull final State state
  )
  {
    _correlations.put(label, ApplicationEntityReference.of(state));
  }

  public void decorrelate (@NonNull final String label) {
    _correlations.remove(label);
  }

  public @NonNull ApplicationEntityReference<State> getCorrelation (@NonNull final String label) {
    return _correlations.get(label);
  }

  public @NonNull Map<@NonNull String, @NonNull ApplicationEntityReference<State>> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }

  public void setCorrelations (
    @Nullable final Map<@NonNull String, @NonNull ApplicationEntityReference<State>> correlations
  )
  {
    _correlations.clear();

    if (correlations != null) {
      _correlations.putAll(correlations);
    }
  }

  @ValidApplicationEntityReference
  @Required
  public @NonNull Set<@NonNull ApplicationEntityReference<State>> getCorrelated () {
    return Collections.unmodifiableSet(new HashSet<>(_correlations.values()));
  }
}
