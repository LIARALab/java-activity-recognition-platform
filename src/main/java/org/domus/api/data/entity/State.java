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
package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Immutable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
@Immutable
public class State
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "identifier")
  private int  _identifier;
  
  @Column(name = "created_at")
  private LocalDateTime _creationDate;

  @Column(name = "updated_at")
  private LocalDateTime _updateDate;

  @Column(name = "deleted_at")
  private LocalDateTime _deletionDate;

  @Column(name = "date")
  private LocalDateTime _date;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "sensor_identifier")
  private Sensor _sensor;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getCreationDate () {
    return this._creationDate;
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getDate () {
    return this._date;
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getDeletionDate () {
    return this._deletionDate;
  }

  public int getIdentifier () {
    return this._identifier;
  }

  @JsonIgnore
  public Sensor getSensor () {
    return this._sensor;
  }

  public int getSensorIdentifier () {
    return this._sensor.getIdentifier();
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getUpdateDate () {
    return this._updateDate;
  }

  public void setDate (@NonNull final LocalDateTime date) {
    this._date = date;
  }
  
  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + _identifier;
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    State other = (State) obj;
    if (_identifier != other._identifier) return false;
    return true;
  }
}
