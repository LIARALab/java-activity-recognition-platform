package org.liara.recognition.presence;

import java.util.NoSuchElementException;

import org.liara.api.data.entity.PresenceState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class LiaraPresenceStream implements PresenceStream
{
  @NonNull
  private final EventStream _events;
  
  @Nullable
  private Event _lastEvent = null;
  
  @Nullable
  private PresenceState _next = null;
  
  public LiaraPresenceStream (@NonNull final EventStream events) {
    _events = events;
  }

  @Override
  public boolean hasNext () {
    update();
    return _next != null;
  }

  @Override
  public PresenceState next () {
    update();
    if (_next == null) throw new NoSuchElementException();
    
    final PresenceState result = _next;
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
    
    if (_next == null && !_events.hasNext() && _lastEvent != null) {
      _next = toOccuringPresence(_lastEvent);
      _lastEvent = null;
    }
  }

  private PresenceState toOccuringPresence (Event event) {
    final PresenceState result = new PresenceState();
    result.setStart(event.getStart());
    result.setEnd(null);
    result.setNode(event.getSensor().getNodes().get(0));
    result.setEmittionDate(event.getStart());
    return result;  
  }

  private PresenceState toPresence (@NonNull final Event event) {
    final PresenceState result = new PresenceState();
    result.setStart(event.getStart());
    result.setEnd(event.getEnd());
    result.setNode(event.getSensor().getNodes().get(0));
    result.setEmittionDate(event.getEnd());
    return result;
  }
}
