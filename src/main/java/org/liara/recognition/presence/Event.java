/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.recognition.presence;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Event implements Comparable<Event>
{  
  @NonNull
  private final Sensor _sensor;
  
  @NonNull
  private final ZonedDateTime _start;
  
  @Nullable
  private final ZonedDateTime _end;
  
  public Event (
    @NonNull final Sensor sensor,
    @NonNull final ZonedDateTime start
  ) {
    _sensor = sensor;
    _start = start;
    _end = null;
  }
  
  public Event (
    @NonNull final Sensor sensor,
    @NonNull final ZonedDateTime start, 
    @Nullable final ZonedDateTime end
  ) {
    _sensor = sensor;
    _start = start;
    _end = end;
  }
  
  public ZonedDateTime getStart () {
    return _start;
  }
  
  public boolean isOccuring () {
    return _end == null;
  }
  
  public boolean isFinished () {
    return !isOccuring();
  }
  
  public ZonedDateTime getEnd () {
    return _end;
  }
  
  public Sensor getSensor () {
    return _sensor;
  }

  public Event setEnd (@Nullable final ZonedDateTime end) {
    return new Event(_sensor, _start, end);
  }

  public Event setStart (@NonNull final ZonedDateTime start) {
    return new Event(_sensor, start, _end);
  }

  public Event setSensor (@NonNull final Sensor sensor) {
    return new Event(sensor, _start, _end);
  }

  public Duration getDuration () {
    return Duration.between(_start, _end);
  }

  @Override
  public int compareTo (@NonNull final Event other) {
    final int startComparison = _start.compareTo(other.getStart());
    
    if (startComparison == 0) {
      final int endComparison = _end.compareTo(other.getEnd());
      
      if (endComparison == 0) {
        return this._sensor.hashCode() - other.getSensor().hashCode();
      } else {
        return endComparison;
      }
    } else {
      return startComparison;
    }
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_end == null) ? 0 : _end.hashCode());
    result = prime * result + ((_sensor == null) ? 0 : _sensor.hashCode());
    result = prime * result + ((_start == null) ? 0 : _start.hashCode());
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Event other = (Event) obj;
    if (_end == null) {
      if (other._end != null) return false;
    } else if (!_end.equals(other._end)) return false;
    if (_sensor == null) {
      if (other._sensor != null) return false;
    } else if (!_sensor.equals(other._sensor)) return false;
    if (_start == null) {
      if (other._start != null) return false;
    } else if (!_start.equals(other._start)) return false;
    return true;
  }

  @Override
  public String toString () {
    return "Event [_sensor=" + _sensor + ", _start=" + _start + ", _end=" + _end + "]";
  }
}
