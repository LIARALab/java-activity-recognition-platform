package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.liara.api.data.entity.state.ActivationState;
import org.springframework.lang.NonNull;

public class Conjunction
{
  @NonNull
  private final List<ActivationState> _states = new ArrayList<>();
  
  public Conjunction (@NonNull final ActivationState ...states) {
    _states.addAll(Arrays.asList(states));
  }
  
  public List<ActivationState> getStates () {
    return Collections.unmodifiableList(_states);
  }
}
