package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Tuple;

import org.liara.api.data.entity.state.ActivationState;
import org.springframework.lang.NonNull;

public class Conjunction
{
  @NonNull
  private final List<ActivationState> _states = new ArrayList<>();
  
  public Conjunction (
    @NonNull final ActivationState ...states
  ) {
    _states.addAll(Arrays.asList(states));
  }
  
  public Conjunction (@NonNull final Tuple tuple) {    
    for (int index = 0; index < tuple.getElements().size(); ++index) {
      _states.add(tuple.get(index, ActivationState.class));
    }
  }
  
  public List<ActivationState> getStates () {
    return Collections.unmodifiableList(_states);
  }
  
  public ZonedDateTime getStart () {
    ZonedDateTime result = _states.get(0).getStart();
    
    for (final ActivationState state : _states) {
      if (state.getStart().compareTo(result) > 0) {
        result = state.getStart();
      }
    }
    
    return result;
  }
  
  public ZonedDateTime getEnd () {
    ZonedDateTime result = _states.get(0).getEnd();
    
    for (final ActivationState state : _states) {
      if (state.getEnd().compareTo(result) < 0) {
        result = state.getEnd();
      }
    }
    
    return result;
  }
  
  public ZonedDateTime getEmittionDate () {
    ZonedDateTime result = _states.get(0).getEnd();
    
    for (final ActivationState state : _states) {
      if (state.getEnd().compareTo(result) > 0) {
        result = state.getEnd();
      }
    }
    
    return result;
  }
}
