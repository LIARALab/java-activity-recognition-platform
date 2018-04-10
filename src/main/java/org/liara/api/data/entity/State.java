/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
public class State extends ApplicationEntity
{
  @Column(name = "emitted_at", nullable = false, updatable = true, unique = false)
  private LocalDateTime _emittionDate;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "sensor_identifier", nullable = false, unique = false, updatable = true)
  private Sensor _sensor;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  @JsonIgnore
  public Sensor getSensor () {
    return _sensor;
  }
  
  public void setSensor (@NonNull final Sensor sensor) {
    _sensor = sensor;
  }

  public long getSensorIdentifier () {
    return _sensor.getIdentifier();
  }

  public void setEmittionDate (@NonNull final LocalDateTime emittionDate) {
    _emittionDate = emittionDate;
  }
}
