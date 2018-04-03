package org.domus.recognition;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;

import org.springframework.lang.NonNull;

public class EventTimeLines<Key>
{
  @NonNull
  private final Map<Key, SortedSet<Event>> _timeLines;
  
  public EventTimeLines () {
    _timeLines = new HashMap<>();
  }
  
  public Event occuring (@NonNull final Key key) {
    if (_timeLines.containsKey(key)) {
      final SortedSet<Event> events = _timeLines.get(key);
      if (events.size() > 0) {
        final Event last = events.last();
        return (last.isOccuring()) ? last : null;
      }
    }
    
    return null;
  }
  
  public Event last (@NonNull final Key key) {
    if (_timeLines.containsKey(key)) {
      final SortedSet<Event> events = _timeLines.get(key);
      final Event last = events.last();
      
      if (last.isOccuring()) {
        return events.subSet(events.first(), last).last();
      } else {
        return last;
      }
    } else {
      return null;
    }
  }
  
  public Event first (@NonNull final Key key) {
    if (_timeLines.containsKey(key)) {
      final SortedSet<Event> events = _timeLines.get(key);
      return events.first();
    } else {
      return null;
    }
  }
  
  public void begin (@NonNull final Key key, @NonNull final LocalDateTime start) {
    if (this.hasOccuringEvent(key)) {
      throw new Error("Unnable to begin a new event for the given key : an event is already occuring for the given timeline.");
    } else {
      if (!this._timeLines.containsKey(key)) {
        this._timeLines.put(key, new TreeSet<>());
      }
      
      this._timeLines.get(key).add(new Event(start));
    }
  }
  
  public Event terminate (@NonNull final Key key, @NonNull final LocalDateTime end) {
    if (this.hasOccuringEvent(key)) {
      final SortedSet<Event> events = this._timeLines.get(key);
      final Event occuring = events.last();
      
      events.remove(occuring);
      events.add(occuring.terminate(end));
      
      return events.last();
    } else {
      throw new Error("Unnable to terminate an event for the given key : no event is already occuring for the given timeline.");
    }
  }
  
  public void terminateAll (@NonNull final LocalDateTime end) {
    for (final Key key : this._timeLines.keySet()) {
      if (this.hasOccuringEvent(key)) this.terminate(key, end);
    }
  }
  
  public void forget (@NonNull final Key key) {
    if (this.hasOccuringEvent(key)) {
      final SortedSet<Event> events = this._timeLines.get(key);
      events.remove(events.last());
    }
  }
  
  public long countOccuringEvents () {
    return this._timeLines.keySet().stream().filter(this::hasOccuringEvent).collect(Collectors.counting());
  }
  
  public Key getEarlierOccuringEventKey () {
    final Entry<Key, SortedSet<Event>> min = this._timeLines.entrySet().stream().reduce(null, (a, b) -> {
      final Event bEvent = b.getValue().last();
      
      if (a == null) {
        return bEvent.isOccuring() ? b : a;
      } else {
        final Event aEvent = a.getValue().last();
        
        return (bEvent.isOccuring() && bEvent.compareTo(aEvent) < 0)  ? b : a;
      }
    });
    
    return (min == null) ? null : min.getKey();
  }

  public boolean hasOccuringEvent (@NonNull final Key key) {
    return this.occuring(key) != null;
  }
  
  public SortedSet<Event> occuringEvents () {
    final SortedSet<Event> events = new TreeSet<>();
    
    for (final Map.Entry<Key, SortedSet<Event>> entry : _timeLines.entrySet()) {
      if (this.hasOccuringEvent(entry.getKey())) {
        events.add(entry.getValue().last());
      }
    }
    
    return events;
  }
  
  public Key keyOf (@NonNull final Event event) {
    for (final Map.Entry<Key, SortedSet<Event>> entry : _timeLines.entrySet()) {
      if (entry.getValue().contains(event)) {
        return entry.getKey();
      }
    }
    
    return null;
  }

  public Set<Key> keySet () {
    return this._timeLines.keySet();
  }
}
