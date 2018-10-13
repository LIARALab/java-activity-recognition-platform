package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sensors")
@JsonPropertyOrder({ "identifier", "name", "type", "node_identifier", "configuration" })
public class Sensor
  extends ApplicationEntity
{
  @Nullable
  private String _name;

  @Nullable
  private String _type;

  @Nullable
  private String _unit;

  @NonNull
  private final Set<@NonNull State> _states;

  @Nullable
  private Node _node;

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
  }

  public Sensor (@NonNull final Sensor sensor) {
    super(sensor);
    _name = sensor.getName();
    _type = sensor.getType();
    _unit = sensor.getUnit();
    _states = new HashSet<>(sensor.getStates());
    _node = sensor.getNode();
    _configuration = sensor.getConfiguration();
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

  @JsonIgnore
  @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
  public @NonNull Set<@NonNull State> getStates () {
    return Collections.unmodifiableSet(_states);
  }

  public void setStates (@Nullable final Collection<@NonNull State> states) {
    removeAllStates();
    if (states != null) addStates(states);
  }

  public void addState (@NonNull final State state) {
    if (!_states.contains(state)) {
      _states.add(state);
      state.setSensor(this);
    }
  }

  public void addStates (@NonNull final Iterable<@NonNull State> states) {
    for (@NonNull final State state : states) { addState(state); }
  }

  public void removeState (@NonNull final State state) {
    if (_states.contains(state)) {
      _states.remove(state);
      state.setSensor(null);
    }
  }

  public void removeAllStates () {
    while (_states.size() > 0) { removeState(_states.iterator().next()); }
  }

  @Column(name = "type", nullable = false, updatable = true, unique = false)
  public @Nullable String getType () {
    return _type;
  }

  public void setType (@Nullable final String type) {
    _type = type;
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

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends Sensor> getReference () {
    return ApplicationEntityReference.of(this);
  }

  public @NonNull Sensor clone () {
    return new Sensor(this);
  }
}

