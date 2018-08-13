package org.liara.api.data;

import org.liara.api.data.entity.state.ActivationState;
import org.springframework.lang.NonNull;

import javax.persistence.Tuple;
import java.time.ZonedDateTime;
import java.util.*;

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

  public Conjunction (@NonNull final Collection<ActivationState> states) {
    _states.addAll(states);
  }

  public boolean contains (@NonNull final ActivationState state) {
    return _states.contains(state);
  }
  
  public List<ActivationState> getStates () {
    return Collections.unmodifiableList(_states);
  }
  
  public ZonedDateTime getStart () {
    ZonedDateTime result = _states.get(0).getStart();
    
    for (final ActivationState state : _states) {
      if (state.getStart() != null) {
        if (result == null || state.getStart()
                                   .compareTo(result) > 0) {
          result = state.getStart();
        }
      }
    }
    
    return result;
  }
  
  public ZonedDateTime getEnd () {
    ZonedDateTime result = _states.get(0).getEnd();
    
    for (final ActivationState state : _states) {
      if (state.getEnd() != null) {
        if (result == null || state.getEnd()
                                   .compareTo(result) < 0) {
          result = state.getEnd();
        }
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

  @Override
  public boolean equals (@NonNull final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof Conjunction) {
      final Conjunction otherConjunction = Conjunction.class.cast(other);

      for (final ActivationState state : _states) {
        if (otherConjunction.contains(state) == false) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hashCode(_states);
  }
}
