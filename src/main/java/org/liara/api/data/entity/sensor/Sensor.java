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

import org.liara.api.data.collection.NodeCollection;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "sensors")
@UseCreationSchema(SensorCreationSchema.class)
public class      Sensor 
       extends    ApplicationEntity
{
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  private String        _name;
  
  @Column(name = "type", nullable = false, updatable = true, unique = false)
  private String        _type;
  
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
  
  @Convert(converter = SensorConfigurationConverter.class)
  @Column(name = "configuration", nullable = false, updatable = false, unique = false)
  private SensorConfiguration _configuration;
  
  @Column(name = "virtual", nullable = false, updatable = true, unique = false)
  private boolean _virtual;
  
  public Sensor () { }
  
  public Sensor (
    @NonNull final SensorCreationSchema schema,
    @NonNull final NodeCollection nodes
  ) {
    _name = schema.getName();
    _type = schema.getType();
    _valueUnit = schema.getOptionalValueUnit().orElse("no-unit");
    _valueLabel = schema.getOptionalValueLabel().orElse("no-unit");
    _ipv4Address = schema.getIpv4Address();
    _ipv6Address = schema.getIpv6Address();
    _node = nodes.findByIdentifier(schema.getParent()).get();
    _configuration = schema.getConfiguration();
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
  
  @PrePersist
  protected void onCreate () {
    super.onCreate();
    _virtual = isVirtual();
  }
  
  @PreUpdate
  protected void onUpdate () {
    super.onUpdate();
    _virtual = isVirtual();
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
  
  public SensorConfiguration getConfiguration () {
    return _configuration;
  }
  
  @Override
  public SensorSnapshot snapshot () {
    return new SensorSnapshot(this);
  }
}
