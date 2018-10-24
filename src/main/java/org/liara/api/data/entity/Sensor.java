package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.utils.CloneMemory;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sensors")
@JsonPropertyOrder({ "identifier", "name", "type", "node_identifier", "configuration" })
public class Sensor<SensorState extends State>
  extends ApplicationEntity
{
  @Nullable
  private String _name;

  @Nullable
  private String _type;

  @Nullable
  private String _unit;

  @NonNull
  private final Set<@NonNull SensorState> _states;

  @Nullable
  private Node _node;

  @Nullable
  private Boolean _isVirtual;

  @Nullable
  private SensorConfiguration _configuration;

  public Sensor () {
    super();
    _name = null;
    _type = null;
    _unit = null;
    _states = new HashSet<>();
    _node = null;
    _configuration = null;
    _isVirtual = null;
  }

  public Sensor (@NonNull final Sensor<SensorState> toCopy, @NonNull final CloneMemory clones) {
    super(toCopy, clones);
    _name = toCopy.getName();
    _type = toCopy.getType();
    _unit = toCopy.getUnit();
    _node = toCopy.getNode() == null ? null : clones.clone(toCopy.getNode());
    _configuration = toCopy.getConfiguration();
    _isVirtual = toCopy.isVirtual();
    _states = new HashSet<>();

    for (@NonNull final SensorState state : toCopy.getStates()) {
      _states.add(clones.clone(state));
    }
  }

  @Column(name = "name", nullable = false, updatable = true, unique = false)
  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }

  @JsonIgnore
  @ManyToOne(optional = false)
  @JoinColumn(name = "node_identifier", nullable = false, unique = false, updatable = false)
  public @Nullable Node getNode () {
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


  @Column(name = "type", nullable = false, updatable = true, unique = false)
  public @Nullable String getType () {
    return _type;
  }

  public void setType (@Nullable final String type) {
    _type = type;
  }

  @JsonIgnore
  @Transient
  public @Nullable Class<?> getTypeClass () {
    try {
      return (_type == null) ? null : Class.forName(_type);
    } catch (final ClassNotFoundException exception) {
      throw new Error("Invalid sensor type " + _type + ", no class found for the given type.");
    }
  }

  @Transient
  public void setTypeClass (@Nullable final Class<?> type) {
    _type = (type == null) ? null : type.getTypeName();
  }

  @JsonIgnore
  @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
  public @NonNull Set<@NonNull SensorState> getStates () {
    return Collections.unmodifiableSet(_states);
  }

  public void setStates (@Nullable final Collection<@NonNull SensorState> states) {
    removeAllStates();
    if (states != null) addStates(states);
  }

  public void addState (@NonNull final SensorState state) {
    if (!_states.contains(state)) {
      _states.add(state);
      state.setSensor(this);
    }
  }

  public void addStates (@NonNull final Iterable<@NonNull SensorState> states) {
    for (@NonNull final SensorState state : states) { addState(state); }
  }

  public void removeState (@NonNull final SensorState state) {
    if (_states.contains(state)) {
      _states.remove(state);
      state.setSensor(null);
    }
  }

  public void removeAllStates () {
    while (_states.size() > 0) { removeState(_states.iterator().next()); }
  }

  @Column(name = "unit", nullable = true, updatable = true, unique = false)
  public @Nullable String getUnit () {
    return _unit;
  }

  public void setUnit (@Nullable final String unit) {
    _unit = unit;
  }

  @Column(name = "configuration", nullable = false, updatable = false, unique = false)
  @Transient
  public @Nullable SensorConfiguration getConfiguration () {
    return _configuration;
  }

  @Transient
  public void setConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
  }

  @Transient
  public boolean isOfType (@NonNull final Class<?> type) {
    return _type.equals(type.getName());
  }

  @Column(name = "is_virtual_sensor", nullable = false, updatable = true, unique = false)
  public @Nullable Boolean isVirtual () {
    return _isVirtual;
  }

  public void setVirtual (@Nullable final Boolean isVirtual) {
    _isVirtual = isVirtual;
  }

  @Transient
  public @Nullable Boolean isNative () {
    return !_isVirtual;
  }

  @Transient
  public void setNative (@Nullable final Boolean isNative) {
    _isVirtual = (isNative == null) ? null : !isNative;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends Sensor> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull Sensor clone ()
  {
    return clone(new CloneMemory());
  }

  @Override
  public @NonNull Sensor clone (@NonNull final CloneMemory clones)
  {
    return new Sensor(this, clones);
  }
}

