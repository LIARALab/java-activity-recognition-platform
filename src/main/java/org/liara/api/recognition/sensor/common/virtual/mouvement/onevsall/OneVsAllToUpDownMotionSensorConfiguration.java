package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class OneVsAllToUpDownMotionSensorConfiguration
       implements SensorConfiguration
{  
  @NonNull
  private final Set<ApplicationEntityReference<Sensor>> _validInputs = new HashSet<>();
  
  @NonNull
  private final Set<ApplicationEntityReference<Sensor>> _ignoredInputs = new HashSet<>();
  
  public OneVsAllToUpDownMotionSensorConfiguration () { }
  
  public OneVsAllToUpDownMotionSensorConfiguration (
    @NonNull final OneVsAllToUpDownMotionSensorConfiguration toCopy
  ) {
    _validInputs.addAll(toCopy.getValidInputs());
    _ignoredInputs.addAll(toCopy.getIgnoredInputs());
  }
  
  public OneVsAllToUpDownMotionSensorConfiguration (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> validInputs,
    @NonNull final Collection<ApplicationEntityReference<Sensor>> ignoredInputs
  ) {
    _validInputs.addAll(validInputs);
    _ignoredInputs.addAll(ignoredInputs);
  }
  
  public boolean isValidInput (@NonNull final Sensor sensor) {
    return _validInputs.contains(ApplicationEntityReference.of(sensor));
  }
  
  public boolean isValidInput (@NonNull final Long sensor) {
    return _validInputs.contains(ApplicationEntityReference.of(Sensor.class, sensor));
  }

  public boolean isValidInput (@NonNull final State state) {
    return _validInputs.contains(ApplicationEntityReference.of(state.getSensor()));
  }
  
  public Iterable<ApplicationEntityReference<Sensor>> validInputs () {
    return Collections.unmodifiableSet(_validInputs);
  }
  
  public Iterable<ApplicationEntityReference<Sensor>> ignoredInputs () {
    return Collections.unmodifiableSet(_ignoredInputs);
  }
  
  @ValidApplicationEntityReference
  public Set<ApplicationEntityReference<Sensor>> getValidInputs () {
    return Collections.unmodifiableSet(_validInputs);
  }
  
  @JsonSetter
  public void setValidInputs (@Nullable final Collection<Long> inputs) {
    _validInputs.clear();
    
    if (inputs != null) {
      _validInputs.addAll(ApplicationEntityReference.of(Sensor.class, inputs));
    }
  }
  
  public boolean isIgnoredInput (@NonNull final Sensor sensor) {
    return _ignoredInputs.contains(ApplicationEntityReference.of(sensor));
  }
  
  public boolean isIgnoredInput (@NonNull final Long sensor) {
    return _ignoredInputs.contains(ApplicationEntityReference.of(Sensor.class, sensor));
  }

  public boolean isIgnoredInput (@NonNull final State state) {
    return _ignoredInputs.contains(ApplicationEntityReference.of(state.getSensor()));
  }
  
  @ValidApplicationEntityReference()
  public Set<ApplicationEntityReference<Sensor>> getIgnoredInputs () {
    return Collections.unmodifiableSet(_ignoredInputs);
  }
  
  @JsonSetter
  public void setIgnoredInputs (@Nullable final Collection<Long> ignored) {
    _ignoredInputs.clear();
    
    if (ignored != null) {
      _ignoredInputs.addAll(ApplicationEntityReference.of(Sensor.class, ignored));
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
  
  public OneVsAllToUpDownMotionSensorConfiguration clone () {
    return new OneVsAllToUpDownMotionSensorConfiguration(this);
  }
}
