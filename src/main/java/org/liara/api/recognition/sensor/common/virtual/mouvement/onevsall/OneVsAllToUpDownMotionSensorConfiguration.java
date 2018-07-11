package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.collection.SensorCollection;
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
  private final Set<Long> _validInputs = new HashSet<>();
  
  @NonNull
  private final Set<Long> _ignoredInputs = new HashSet<>();
  
  public OneVsAllToUpDownMotionSensorConfiguration () { }
  
  public OneVsAllToUpDownMotionSensorConfiguration (
    @NonNull final OneVsAllToUpDownMotionSensorConfiguration toCopy
  ) {
    _validInputs.addAll(toCopy.getValidInputs());
    _ignoredInputs.addAll(toCopy.getIgnoredInputs());
  }
  
  public boolean isValidInput (@NonNull final Sensor sensor) {
    return _validInputs.contains(sensor.getIdentifier());
  }
  
  public boolean isValidInput (@NonNull final Long sensor) {
    return _validInputs.contains(sensor);
  }

  public boolean isValidInput (@NonNull final State state) {
    return _validInputs.contains(state.getSensorIdentifier());
  }
  
  public Iterable<Long> validInputs () {
    return Collections.unmodifiableSet(_validInputs);
  }
  
  public Iterable<Long> ignoredInputs () {
    return Collections.unmodifiableSet(_ignoredInputs);
  }
  
  @ValidApplicationEntityReference(collection = SensorCollection.class)
  public Set<Long> getValidInputs () {
    return Collections.unmodifiableSet(_validInputs);
  }
  
  @JsonSetter
  public void setValidInputs (@Nullable final Collection<Long> inputs) {
    _validInputs.clear();
    
    if (inputs != null) {
      _validInputs.addAll(inputs);
    }
  }
  
  public boolean isIgnoredInput (@NonNull final Sensor sensor) {
    return _ignoredInputs.contains(sensor.getIdentifier());
  }
  
  public boolean isIgnoredInput (@NonNull final Long sensor) {
    return _ignoredInputs.contains(sensor);
  }

  public boolean isIgnoredInput (@NonNull final State state) {
    return _ignoredInputs.contains(state.getSensorIdentifier());
  }
  
  @ValidApplicationEntityReference(collection = SensorCollection.class)
  public Set<Long> getIgnoredInputs () {
    return Collections.unmodifiableSet(_ignoredInputs);
  }
  
  @JsonSetter
  public void setIgnoredInputs (@Nullable final Collection<Long> ignored) {
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
  
  public OneVsAllToUpDownMotionSensorConfiguration clone () {
    return new OneVsAllToUpDownMotionSensorConfiguration(this);
  }
}
