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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nodes")
public class Node
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "identifier")
  private Long          _identifier;

  @Column(name = "created_at")
  @NonNull
  private LocalDateTime _creationDate;

  @Column(name = "updated_at")
  @NonNull
  private LocalDateTime _updateDate;

  @Column(name = "deleted_at")
  @Nullable
  private LocalDateTime _deletionDate;

  @Column(name = "name")
  @NonNull
  private String        _name;

  @Column(name = "start")
  private int           _start;

  @Column(name = "end")
  private int           _end;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "sensors_by_nodes",
      joinColumns = @JoinColumn(name = "node_identifier"),
      inverseJoinColumns = @JoinColumn(name = "sensor_identifier")
  )
  private List<Sensor>  _sensors;

  public void delete () {
    this._deletionDate = LocalDateTime.now();
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getCreationDate () {
    return this._creationDate;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getDeletionDate () {
    return this._deletionDate;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getUpdateDate () {
    return this._updateDate;
  }

  public int getEnd () {
    return this._end;
  }

  public long getIdentifier () {
    return this._identifier;
  }

  public String getName () {
    return this._name;
  }

  @JsonIgnore
  public List<Sensor> getSensors () {
    return this._sensors;
  }

  public int getStart () {
    return this._start;
  }

  public void restore () {
    this._deletionDate = null;
  }

  public void setName (@NonNull final String name) {
    this._name = name;
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + _identifier.intValue();
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Node other = (Node) obj;
    if (_identifier != other._identifier) return false;
    return true;
  }
}
