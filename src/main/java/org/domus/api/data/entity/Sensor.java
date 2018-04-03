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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sensors")
public class Sensor
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "identifier")
  private Long           _identifier;

  @Column(name = "created_at")
  private LocalDateTime _creationDate;

  @Column(name = "updated_at")
  private LocalDateTime _lastUpdateDate;

  @Column(name = "deleted_at")
  private LocalDateTime _deletionDate;

  @Column(name = "name")
  private String        _name;
  
  @Column(name = "type")
  private String        _type;

  @Column(name = "value_type")
  private String        _valueType;

  @Column(name = "value_unit")
  private String        _valueUnit;

  @Column(name = "value_label")
  private String        _valueLabel;

  @Column(name = "ipv4_address")
  private String        _ipv4Address;

  @Column(name = "ipv6_address")
  private String        _ipv6Address;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "sensor_identifier")
  private List<State>   _states;

  @ManyToMany()
  @JoinTable(
      name = "sensors_by_nodes",
      joinColumns = @JoinColumn(name = "sensor_identifier"),
      inverseJoinColumns = @JoinColumn(name = "node_identifier")
  )
  private List<Node>    _nodes;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getCreationDate () {
    return _creationDate;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getDeletionDate () {
    return _deletionDate;
  }

  public long getIdentifier () {
    return _identifier;
  }

  public String getIpv4Address () {
    return _ipv4Address;
  }

  public String getIpv6Address () {
    return _ipv6Address;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public LocalDateTime getLastUpdateDate () {
    return _lastUpdateDate;
  }

  public String getName () {
    return _name;
  }

  @JsonIgnore
  public List<Node> getNodes () {
    return _nodes;
  }

  @JsonIgnore
  public List<State> getStates () {
    return _states;
  }

  public String getType () {
    return _type;
  }

  public String getValueType () {
    return _valueType;
  }

  public String getValueUnit () {
    return _valueUnit;
  }

  public String getValueLabel () {
    return _valueLabel;
  }

  public void setIpv4Address (@NonNull final String ipv4Address) {
    _ipv4Address = ipv4Address;
  }

  public void setIpv6Address (@NonNull final String ipv6Address) {
    _ipv6Address = ipv6Address;
  }

  public void setName (@NonNull final String name) {
    _name = name;
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_identifier == null) ? 0 : _identifier.hashCode());
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Sensor other = (Sensor) obj;
    if (_identifier == null) {
      if (other._identifier != null) return false;
    } else if (!_identifier.equals(other._identifier)) return false;
    return true;
  }
}
