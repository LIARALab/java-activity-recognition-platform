package org.domus.recognition;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Event implements Comparable<Event>
{  
  @NonNull
  private final LocalDateTime _start;
  
  @Nullable
  private final LocalDateTime _end;
  
  public Event (@NonNull final LocalDateTime start) {
    _start = start;
    _end = null;
  }
  
  public Event (
    @NonNull final LocalDateTime start, 
    @Nullable final LocalDateTime end
  ) {
    _start = start;
    _end = end;
  }
  
  public LocalDateTime getStart () {
    return this._start;
  }
  
  public boolean isOccuring () {
    return this._end == null;
  }
  
  public boolean isFinished () {
    return !this.isOccuring();
  }
  
  public LocalDateTime getEnd () {
    return this._end;
  }

  @Override
  public int compareTo (@NonNull final Event other) {
    return this._start.compareTo(other.getStart());
  }

  public Event terminate (@NonNull final LocalDateTime end) {
    return new Event(_start, end);
  }
  
  public boolean hasEndOverlap (@NonNull final Event other) {
    return this.getEnd().isAfter(other.getStart());
  }
  
  public boolean hasStartOverlap (@NonNull final Event other) {
    return other.hasEndOverlap(this);
  }
  
  public Event resolveStart (@NonNull final Event other) {
    if (this._start.isBefore(other.getEnd())) {
      return this.resolveStartOverlap(other);
    } else {
      return this.resolveStartVoid(other);
    }
  }
  
  public Event resolveEnd (@NonNull final Event other) {
    if (this._end.isAfter(other.getStart())) {
      return this.resolveEndOverlap(other);
    } else {
      return this.resolveEndVoid(other);
    }
  }
  
  public Event resolveEndVoid (@NonNull final Event other) {
    return new Event(
      _start,
      _end.plus(Duration.between(_end, other.getStart()).dividedBy(2))
    );
  }
  
  public Event resolveStartVoid (@NonNull final Event other) {
    return new Event(
      other.getEnd().plus(Duration.between(other.getEnd(), _start)),
      _end
    );
  }
  
  public Event resolveEndOverlap (@NonNull final Event other) {
    return new Event(
      _start,
      other.getStart().plus(Duration.between(other.getStart(), _end).dividedBy(2))
    );
  }
  
  public Event resolveStartOverlap (@NonNull final Event other) {
    return new Event(
      _start.plus(Duration.between(_start, other.getEnd())),
      _end
    );
  }

  public Event setEnd (@NonNull final LocalDateTime end) {
    return new Event(
      _start,
      end
    );
  }

  public Event setStart (@NonNull final LocalDateTime start) {
    return new Event(
      start,
      _end
    );
  }

  public Duration getDuration () {
    return Duration.between(_start, _end);
  }
}
