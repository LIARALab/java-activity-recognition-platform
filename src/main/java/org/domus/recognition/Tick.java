package org.domus.recognition;

import java.time.Duration;
import java.time.LocalDateTime;
import org.domus.api.data.entity.Sensor;
import org.domus.api.data.entity.BooleanState;

import org.springframework.lang.NonNull;

public class Tick implements Comparable<Tick>
{
  @NonNull private final Sensor _sensor;
  @NonNull private final LocalDateTime _date;
  @NonNull private final boolean _up;

  public static Tick before (@NonNull final Tick tick) {
    return Tick.before(tick, Duration.ofMillis(1));
  }

  public static Tick before (@NonNull final Tick tick, @NonNull final Duration duration) {
    return new Tick(
      tick.getSensor(),
      tick.getDate().minus(duration),
      tick.isUp()
    );
  }

  public static Tick after (@NonNull final Tick tick) {
    return Tick.after(tick, Duration.ofMillis(1));
  }

  public static Tick after (@NonNull final Tick tick, @NonNull final Duration duration) {
    return new Tick(
      tick.getSensor(),
      tick.getDate().plus(duration),
      tick.isUp()
    );
  }
  
  public Tick (@NonNull final BooleanState state) {
    _sensor = state.getSensor();
    _date = state.getDate();
    _up = state.getValue();
  }
  
  public Tick (
    @NonNull final Sensor sensor, 
    @NonNull final LocalDateTime date, 
    boolean up
  ) {
    _sensor = sensor;
    _date = date;
    _up = up;
  }

  public Sensor getSensor () {
    return _sensor;
  }
  
  public LocalDateTime getDate () {
    return _date;
  }
  
  public boolean isUp () {
    return _up;
  }
  
  public boolean isDown () {
    return !_up;
  }

  @Override
  public int compareTo (@NonNull final Tick other) {
    final int dateComparison = _date.compareTo(other.getDate());
    
    if (dateComparison == 0) {
      return this.getSensor().hashCode() - other.getSensor().hashCode();
    } else {
      return dateComparison;
    }
  }

  public Tick setDown () {
    return new Tick (this.getSensor(), this.getDate(), false);
  }
  
  public Tick setUp () {
    return new Tick (this.getSensor(), this.getDate(), true);
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
  public boolean equals (Object obj) {
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
    return "Tick [_sensor=" + _sensor + ", _date=" + _date + ", _up=" + _up + "]";
  }
}
