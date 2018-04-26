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
package org.liara.api.data.entity.state;

import java.time.Duration;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.liara.api.data.entity.node.Node;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "states_presence")
@PrimaryKeyJoinColumn(name = "state_identifier")
public class ActivationState extends State
{  
  @Column(name = "start", nullable = false, updatable = true, unique = false)
  @NonNull
  private ZonedDateTime _start;

  @Column(name = "end", nullable = true, updatable = true, unique = false)
  @Nullable
  private ZonedDateTime _end;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = true)
  private Node _node;
  
  @Formula("DATEDIFF(start, end) * 24 * 3600 + TIMESTAMPDIFF(MICROSECOND, start, end) / 1000")
  private Long _milliseconds;
  
  public Duration getDuration () {
    if (this.getEnd() == null) {
      return null;
    } else {
      return Duration.between(_start, _end);
    }
  }
  
  public Long getMilliseconds () {
    return _milliseconds;
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public ZonedDateTime getEnd () {
    return _end;
  }

  @JsonIgnore
  public Node getNode () {
    return _node;
  }
  
  public Long getNodeIdentifier () {
    return _node.getIdentifier();
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public ZonedDateTime getStart () {
    return _start;
  }
  
  public void merge (@NonNull final ActivationState other) {
    if (_start.compareTo(other.getStart()) <= 0) {
      _end = other.getEnd();
    } else {
      _start = other.getStart();
    }
  }

  public void setEnd (@NonNull final ZonedDateTime end) {
    _end = end;
  }

  public void setNode (@NonNull final Node node) {
    _node = node;
  }

  public void setStart (@NonNull final ZonedDateTime start) {
    _start = start;
  }

  @Override
  public String toString () {
    StringBuilder builder = new StringBuilder();
    builder.append("PresenceState [");
    if (getIdentifier() != null) {
      builder.append("getIdentifier()=");
      builder.append(getIdentifier());
      builder.append(", ");
    }
    if (getEmittionDate() != null) {
      builder.append("getEmittionDate()=");
      builder.append(getEmittionDate());
      builder.append(", ");
    }
    builder.append("getSensorIdentifier()=");
    builder.append(getSensorIdentifier());
    builder.append(", ");
    if (getCreationDate() != null) {
      builder.append("getCreationDate()=");
      builder.append(getCreationDate());
      builder.append(", ");
    }
    if (getDeletionDate() != null) {
      builder.append("getDeletionDate()=");
      builder.append(getDeletionDate());
      builder.append(", ");
    }
    if (getUpdateDate() != null) {
      builder.append("getUpdateDate()=");
      builder.append(getUpdateDate());
      builder.append(", ");
    }
    if (_start != null) {
      builder.append("_start=");
      builder.append(_start);
      builder.append(", ");
    }
    if (_end != null) {
      builder.append("_end=");
      builder.append(_end);
      builder.append(", ");
    }
    if (_milliseconds != null) {
      builder.append("_milliseconds=");
      builder.append(_milliseconds);
      builder.append(", ");
    }
    if (getNodeIdentifier() != null) {
      builder.append("getNodeIdentifier()=");
      builder.append(getNodeIdentifier());
    }
    builder.append("]");
    return builder.toString();
  }
}
