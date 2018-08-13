package org.liara.api.data.entity.sensor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.ApplicationEntityReference;
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

import javax.persistence.*;
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
  private Set<State> _states;

  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = false)
  private Node    _node;
  
  @Convert(converter = SensorConfigurationConverter.class)
  @Column(name = "configuration", nullable = false, updatable = false, unique = false)
  private SensorConfiguration _configuration;
  
  @Column(name = "is_virtual_sensor", nullable = false, updatable = true, unique = false)
  private boolean _virtual;

  public Sensor () {
    _name = null;
    _type = null;
    _unit = null;
    _states = new HashSet<>();
    _node = null;
    _configuration = null;
  }

  public String getName () {
    return _name;
  }

  public void setName (@NonNull final String name) {
    _name = name;
  }
  
  @PrePersist
  public void willBeCreated () {
    _virtual = isVirtual();
  }
  
  @PreUpdate
  public void willBeUpdated () {
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
  
  @JsonIgnore
  public Iterable<State> states () {
    return getStates();
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
  
  public void setConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
  }
  
  @Override
  public SensorSnapshot snapshot () {
    return new SensorSnapshot(this);
  }  
  
  @Override
  public ApplicationEntityReference<Sensor> getReference () {
    return ApplicationEntityReference.of(this);
  }

  public boolean isOfType (@NonNull final Class<?> type) {
    return type.isAssignableFrom(getTypeClass());
  }
}
