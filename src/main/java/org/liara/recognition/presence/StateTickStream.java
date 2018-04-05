package org.liara.recognition.presence;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.liara.api.data.entity.BooleanState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class StateTickStream implements TickStream
{
  @NonNull
  private final Iterator<BooleanState> _states;
  
  @Nullable
  private Tick _next = null;

  public StateTickStream (@NonNull final Iterator<BooleanState> states) {
    _states = states;
  }

  public StateTickStream (@NonNull final Iterable<BooleanState> states) {
    _states = states.iterator();
  }

  public StateTickStream (@NonNull final BooleanState... states) {
    _states = Arrays.asList(states).iterator();
  }
  
  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  @Override
  public Tick next () {
    update();
    if (_next == null) throw new NoSuchElementException();
    
    final Tick result = _next;
    _next = null;
    return result;
  }
  
  private void update () {
    while (_next == null && _states.hasNext()) {
      final BooleanState state = _states.next();
      
      if (state.getValue() == true) {
        _next = new Tick(state.getSensor(), state.getDate());
      }
    }
  }
}
