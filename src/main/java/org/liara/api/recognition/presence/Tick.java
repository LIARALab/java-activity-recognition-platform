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
package org.liara.api.recognition.presence;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;

public final class Tick implements Comparable<Tick>
{
  @NonNull private final Sensor _sensor;
  @NonNull private final ZonedDateTime _date;
  
  public Tick (@NonNull final BooleanState state) {
    _sensor = state.getSensor();
    _date = state.getEmissionDate();
  }
  
  public Tick (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  ) {
    _sensor = sensor;
    _date = date;
  }

  public Sensor getSensor () {
    return _sensor;
  }
  
  public ZonedDateTime getDate () {
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
      return other._sensor == null;
    } else return _sensor.equals(other._sensor);
  }

  @Override
  public String toString () {
    return "Tick [_sensor=" + _sensor + ", _date=" + _date + "]";
  }
}
