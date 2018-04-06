package org.liara.recognition.presence;

import java.util.NoSuchElementException;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class TickEventStream implements EventStream
{
  @NonNull
  private final TickStream _ticks;
  
  @Nullable
  private Event _next = null;
  
  @Nullable
  private Tick _lastTick = null;
  
  public TickEventStream (@NonNull final TickStream ticks) {
    _ticks = ticks;
  }
  
  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  private void update () {
    while (_next == null && _ticks.hasNext()) {
      final Tick next = _ticks.next();
      
      if (_lastTick == null) {
        _lastTick = next;
      } else if (!_lastTick.getSensor().equals(next.getSensor())) {
        _next = new Event(_lastTick.getSensor(), _lastTick.getDate(), next.getDate());
        _lastTick = next;
      }
    }
    
    if (_next == null && _ticks.hasNext() == false && _lastTick != null) {
      _next = new Event(_lastTick.getSensor(), _lastTick.getDate());
      _lastTick = null;
    }
  }

  @Override
  public Event next () {
    update();

    if (_next == null) throw new NoSuchElementException();
    
    final Event result = _next;
    _next = null;
    return result;
  }

}
