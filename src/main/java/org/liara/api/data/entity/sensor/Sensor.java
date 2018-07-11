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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.database.SensorConfigurationConverter;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.recognition.sensor.VirtualSensorHandler;
import org.liara.api.recognition.sensor.common.NativeSensor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sensors")
@UseCreationSchema(SensorCreationSchema.class)
@JsonPropertyOrder({ "identifier", "name", "type", "node_identifier", "configuration" })
public class      Sensor 
       extends    ApplicationEntity
{
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  private String        _name;
  
  @Column(name = "type", nullable = false, updatable = true, unique = false)
  private String        _type;
  
  @Column(name = "unit", nullable = true, updatable = true, unique = false)
  private String        _unit;

  @OneToMany(
    mappedBy="_sensor", 
    cascade = CascadeType.ALL, 
    orphanRemoval = false,
    fetch = FetchType.LAZY
  )
  private Set<State>   _states = new HashSet<>();

  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = false)
  private Node    _node;
  
  @Convert(converter = SensorConfigurationConverter.class)
  @Column(name = "configuration", nullable = false, updatable = false, unique = false)
  private SensorConfiguration _configuration;
  
  @Column(name = "is_virtual_sensor", nullable = false, updatable = true, unique = false)
  private boolean _virtual;
  
  public Sensor () { }
  
  public Sensor (@NonNull final SensorCreationSchema schema) {
    _name = schema.getName();
    _type = schema.getType();
    _unit = schema.getUnit();
    _node = schema.getParent().resolve();
    _configuration = schema.getConfiguration();
  }

  public String getName () {
    return _name;
  }

  public void setName (@NonNull final String name) {
    _name = name;
  }
  
  @PrePersist
  public void willBeCreated () {
    super.willBeCreated();
    _virtual = isVirtual();
  }
  
  @PreUpdate
  public void willBeUpdated () {
    super.willBeUpdated();
    _virtual = isVirtual();
  }

  @JsonIgnore
  public Node getNode () {
    return _node;
  }
  
  /**
   * Change the parent node of this sensor.
   * 
   * @param node The new parent node of this sensor.
   */
  public void setNode (@Nullable final Node node) {
    if (node != _node) {
      if (_node != null) {
        final Node oldNode = _node;
        _node = null;
        oldNode.removeSensor(this);
      }
      
      _node = node;
      
      if (_node != null) {
        _node.addSensor(this);
      }
    }
  }
  
  public Long getNodeIdentifier () {
    return _node.getIdentifier();
  }

  @JsonIgnore
  public Set<State> getStates () {
    return Collections.unmodifiableSet(_states);
  }
  
  public void addState (@NonNull final State state) {
    if (!_states.contains(state)) {
      _states.add(state);
      state.setSensor(this);
    }
  }
  
  public void removeState (@NonNull final State state) {
    if (_states.contains(state)) {
      _states.remove(state);
      state.setSensor(null);
    }
  }

  public String getType () {
    return _type;
  }
  
  @JsonIgnore
  public Class<?> getTypeClass () {
    try {
      return Class.forName(_type);
    } catch (final ClassNotFoundException exception) {
      throw new Error("Invalid sensor type " + _type + ", no class found for the given type.");
    }
  }
  
  @JsonIgnore
  public Class<? extends State> getStateClass () {
    final Class<?> typeClass = this.getTypeClass();
    final EmitStateOfType annotation = typeClass.getAnnotation(EmitStateOfType.class);
    
    if (annotation == null) {
      throw new Error(String.join(
        "",
        "Unnable to retrieve the emitted state type of this sensor because the ",
        "type of this sensor ", typeClass.toString(), " does not declare any ",
        EmitStateOfType.class.toString(), " annotation."
      ));
    } else {
      return annotation.value();
    }
  }
  
  public boolean isVirtual () {
    return VirtualSensorHandler.class.isAssignableFrom(getTypeClass());
  }
  
  public boolean isNative () {
    return NativeSensor.class.isAssignableFrom(getTypeClass());
  }
  
  public void setType (@NonNull final String type) {
    _type = type;
  }

  public String getUnit () {
    return _unit;
  }
  
  public void setUnit (@NonNull final String unit) {
    _unit = unit;
  }
  
  public SensorConfiguration getConfiguration () {
    return _configuration;
  }
  
  @Override
  public SensorSnapshot snapshot () {
    return new SensorSnapshot(this);
  }
}
