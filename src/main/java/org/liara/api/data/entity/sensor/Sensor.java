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
package org.liara.api.data.entity.sensor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "sensors")
public class Sensor extends ApplicationEntity
{
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  private String        _name;
  
  @Column(name = "type", nullable = false, updatable = true, unique = false)
  private String        _type;

  @Column(name = "value_type", nullable = false, updatable = true, unique = false)
  private String        _valueType;

  @Column(name = "value_unit", nullable = true, updatable = true, unique = false)
  private String        _valueUnit;

  @Column(name = "value_label", nullable = true, updatable = true, unique = false)
  private String        _valueLabel;

  @Column(name = "ipv4_address", nullable = true, updatable = true, unique = false)
  private String        _ipv4Address;

  @Column(name = "ipv6_address", nullable = true, updatable = true, unique = false)
  private String        _ipv6Address;

  @OneToMany(
    mappedBy="_sensor", 
    cascade = CascadeType.ALL, 
    orphanRemoval = false,
    fetch = FetchType.LAZY
  )
  private List<State>   _states;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = false)
  private Node    _node;
  
  public Sensor () { }
  
  public Sensor (@NonNull final SensorCreationSchema schema) {
    _name = schema.getName().get();
    _type = schema.getType().get();
    _valueType = schema.getValueType().get();
    _valueUnit = schema.getValueUnit().orElse("no-unit");
    _valueLabel = schema.getValueLabel().orElse("no-unit");
    _ipv4Address = schema.getIpv4Address().orElse(null);
    _ipv6Address = schema.getIpv6Address().orElse(null);
    _node = schema.getParent();
  }

  public String getIpv4Address () {
    return _ipv4Address;
  }
  
  public void setIpv4Address (@NonNull final String ipv4Address) {
    _ipv4Address = ipv4Address;
  }

  public String getIpv6Address () {
    return _ipv6Address;
  }

  public void setIpv6Address (@NonNull final String ipv6Address) {
    _ipv6Address = ipv6Address;
  }

  public String getName () {
    return _name;
  }

  public void setName (@NonNull final String name) {
    _name = name;
  }

  @JsonIgnore
  public Node getNode () {
    return _node;
  }
  
  public Long getNodeIdentifier () {
    return _node.getIdentifier();
  }

  @JsonIgnore
  public List<State> getStates () {
    return _states;
  }

  public String getType () {
    return _type;
  }
  
  public void setType (@NonNull final String type) {
    _type = type;
  }

  public String getValueType () {
    return _valueType;
  }
  
  public void setValueType (@NonNull final String valueType) {
    _valueType = valueType;
  }

  public String getValueUnit () {
    return _valueUnit;
  }
  
  public void setValueUnit (@NonNull final String valueUnit) {
    _valueUnit = valueUnit;
  }

  public String getValueLabel () {
    return _valueLabel;
  }
  
  public void setValueLabel (@NonNull final String valueLabel) {
    _valueLabel = valueLabel;
  }
}
