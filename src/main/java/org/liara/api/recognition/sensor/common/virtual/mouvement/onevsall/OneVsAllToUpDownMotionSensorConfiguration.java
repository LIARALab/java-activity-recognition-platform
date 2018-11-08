package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.validation.ValidApplicationEntityReference;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OneVsAllToUpDownMotionSensorConfiguration
  implements SensorConfiguration
{  
  @NonNull
  private final Set<@NonNull ApplicationEntityReference<? extends Sensor>> _validInputs;
  
  @NonNull
  private final Set<@NonNull ApplicationEntityReference<? extends Sensor>> _ignoredInputs;

  public OneVsAllToUpDownMotionSensorConfiguration () {
    _validInputs = new HashSet<>();
    _ignoredInputs = new HashSet<>();
  }
  
  public OneVsAllToUpDownMotionSensorConfiguration (
    @NonNull final OneVsAllToUpDownMotionSensorConfiguration toCopy
  ) {
    _validInputs = new HashSet<>(toCopy.getValidInputs());
    _ignoredInputs = new HashSet<>(toCopy.getIgnoredInputs());
  }
  
  public OneVsAllToUpDownMotionSensorConfiguration (
    @NonNull final Collection<@NonNull ApplicationEntityReference<Sensor>> validInputs,
    @NonNull final Collection<@NonNull ApplicationEntityReference<Sensor>> ignoredInputs
  ) {
    _validInputs = new HashSet<>(validInputs);
    _ignoredInputs = new HashSet<>(ignoredInputs);
  }
  
  public boolean isValidInput (@NonNull final Sensor sensor) {
    return _validInputs.contains(ApplicationEntityReference.of(sensor));
  }
  
  public boolean isValidInput (@NonNull final Long sensor) {
    return _validInputs.contains(ApplicationEntityReference.of(Sensor.class, sensor));
  }

  public boolean isValidInput (@NonNull final State state) {
    return _validInputs.contains(state.getSensorIdentifier());
  }

  public @NonNull Iterable<@NonNull ApplicationEntityReference<? extends Sensor>> validInputs () {
    return Collections.unmodifiableSet(_validInputs);
  }

  public @NonNull Iterable<@NonNull ApplicationEntityReference<? extends Sensor>> ignoredInputs () {
    return Collections.unmodifiableSet(_ignoredInputs);
  }
  
  @ValidApplicationEntityReference
  public @NonNull Set<@NonNull ApplicationEntityReference<? extends Sensor>> getValidInputs () {
    return Collections.unmodifiableSet(_validInputs);
  }
  
  @JsonSetter
  public void setValidInputs (
    @Nullable final Collection<@NonNull ApplicationEntityReference<? extends Sensor>> inputs
  )
  {
    _validInputs.clear();

    if (inputs != null) {
      _validInputs.addAll(inputs);
    }
  }
  
  public boolean isIgnoredInput (@NonNull final Sensor sensor) {
    return _ignoredInputs.contains(ApplicationEntityReference.of(sensor));
  }
  
  public boolean isIgnoredInput (@NonNull final Long sensor) {
    return _ignoredInputs.contains(ApplicationEntityReference.of(Sensor.class, sensor));
  }

  public boolean isIgnoredInput (@NonNull final State state) {
    return _ignoredInputs.contains(state.getSensorIdentifier());
  }
  
  @ValidApplicationEntityReference()
  public @NonNull Set<@NonNull ApplicationEntityReference<? extends Sensor>> getIgnoredInputs () {
    return Collections.unmodifiableSet(_ignoredInputs);
  }
  
  @JsonSetter
  public void setIgnoredInputs (
    @Nullable final Collection<@NonNull ApplicationEntityReference<? extends Sensor>> ignored
  )
  {
    _ignoredInputs.clear();
    
    if (ignored != null) {
      _ignoredInputs.addAll(ignored);
    }
  }
  
  public boolean isInvalidInput (@NonNull final Sensor sensor) {
    return !isValidInput(sensor) && !isIgnoredInput(sensor);
  }
  
  public boolean isInvalidInput (@NonNull final Long sensor) {
    return !isValidInput(sensor) && !isIgnoredInput(sensor);
  }

  public boolean isInvalidInput (@NonNull final State state) {
    return !isValidInput(state) && !isIgnoredInput(state);
  }
}
