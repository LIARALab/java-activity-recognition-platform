package org.liara.recognition.presence;

import java.time.LocalDateTime;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public final class Tick implements Comparable<Tick>
{
  @NonNull private final Sensor _sensor;
  @NonNull private final LocalDateTime _date;
  
  public Tick (@NonNull final BooleanState state) {
    _sensor = state.getSensor();
    _date = state.getEmittionDate();
  }
  
  public Tick (
    @NonNull final Sensor sensor, 
    @NonNull final LocalDateTime date
  ) {
    _sensor = sensor;
    _date = date;
  }

  public Sensor getSensor () {
    return _sensor;
  }
  
  public LocalDateTime getDate () {
    return _date;
  }
  
  @Override
  public int compareTo (@NonNull final Tick other) {
    final int dateComparison = _date.compareTo(other.getDate());
    
    if (dateComparison == 0) {
      return getSensor().hashCode() - other.getSensor().hashCode();
    } else {
      return dateComparison;
    }
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_date == null) ? 0 : _date.hashCode());
    result = prime * result + ((_sensor == null) ? 0 : _sensor.hashCode());
    return result;
  }

  @Override
  public boolean equals (@Nullable final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Tick other = (Tick) obj;
    if (_date == null) {
      if (other._date != null) return false;
    } else if (!_date.equals(other._date)) return false;
    if (_sensor == null) {
      if (other._sensor != null) return false;
    } else if (!_sensor.equals(other._sensor)) return false;
    return true;
  }

  @Override
  public String toString () {
    return "Tick [_sensor=" + _sensor + ", _date=" + _date + "]";
  }
}
