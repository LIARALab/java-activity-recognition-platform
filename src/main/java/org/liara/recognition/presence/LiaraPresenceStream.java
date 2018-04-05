package org.liara.recognition.presence;

import java.util.NoSuchElementException;

import org.liara.api.data.entity.Presence;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class LiaraPresenceStream implements PresenceStream
{
  @NonNull
  private final EventStream _events;
  
  @Nullable
  private Event _lastEvent = null;
  
  @Nullable
  private Presence _next = null;
  
  public LiaraPresenceStream (@NonNull final EventStream events) {
    _events = events;
  }

  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  @Override
  public Presence next () {
    update();
    if (_next == null) throw new NoSuchElementException();
    
    final Presence result = _next;
    _next = null;
    return result;
  }

  private void update () {
    while (_next == null && _events.hasNext()) {
      final Event next = _events.next();
      
      if (_lastEvent == null) {
        _lastEvent = next;
      } else {
        if (_lastEvent.getSensor().equals(next.getSensor())) {
          _lastEvent = _lastEvent.setEnd(next.getEnd());
        } else {
          _next = toPresence(_lastEvent);
          _lastEvent = next;
        }
      }
    }
  }

  private Presence toPresence (@NonNull final Event event) {
    return new Presence(event.getStart(), event.getEnd(), event.getSensor().getNodes().get(0));
  }
}
