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
package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.tree.NestedSet;
import org.liara.api.data.tree.NestedSetCoordinates;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "nodes")
@JsonPropertyOrder({"identifier", "type", "name", "coordinates"})
public class Node
  extends ApplicationEntity
  implements NestedSet
{
  @Nullable
  private String _name;

  @Nullable
  private String _type;

  @NonNull
  private final Set<@NonNull Sensor> _sensors;

  @NonNull
  private NestedSetCoordinates _coordinates;

  public Node () {
    _name = null;
    _type = null;
    _coordinates = new NestedSetCoordinates();
    _sensors = new HashSet<>();
  }

  public Node (@NonNull final Node toCopy) {
    _name = toCopy.getName();
    _type = toCopy.getType();
    _coordinates = new NestedSetCoordinates(toCopy.getCoordinates());
    _sensors = new HashSet<>(toCopy.getSensors());
  }

  /**
   * Return the type of this node.
   * <p>
   * A type is a generic name linked to a class that allows to do special operations on this node.
   *
   * @return The type of this node.
   */
  @Column(name = "type", nullable = false, updatable = false, unique = false)
  public @Nullable String getType () {
    return _type;
  }

  public void setType (@Nullable final Class<?> type) {
    _type = (type == null) ? null : type.getName();
  }

  public void setType (@Nullable final String type) {
    _type = type;
  }

  /**
   * Return the name of this node.
   * <p>
   * It's only a label in order to easily know what this node represents.
   *
   * @return The name of this node.
   */
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  public @Nullable String getName () {
    return _name;
  }

  /**
   * Rename this node.
   *
   * @param name The new name to apply to this node.
   */
  public void setName (@Nullable final String name) {
    _name = name;
  }

  /**
   * Return all sensors directly attached to this node.
   *
   * @return All sensors directly attached to this node.
   */
  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "node")
  public @NonNull Set<@NonNull Sensor> getSensors () {
    return Collections.unmodifiableSet(_sensors);
  }

  public void setSensors (@Nullable final Collection<@NonNull Sensor> sensors) {
    removeAllSensors();
    if (sensors != null) addSensors(sensors);
  }

  @Transient
  public @NonNull Set<@NonNull Sensor> getSensorsWithName (@NonNull final String name) {
    @NonNull final Set<@NonNull Sensor> results = new HashSet<>();

    for (final Sensor sensor : _sensors) {
      if (Objects.equals(sensor.getName(), name)) {
        results.add(sensor);
      }
    }

    return results;
  }

  @Transient
  public @NonNull Optional<Sensor> getFirstSensorWithName (@NonNull final String name) {
    for (final Sensor sensor : _sensors) {
      if (Objects.equals(sensor.getName(), name)) {
        return Optional.ofNullable(sensor);
      }
    }

    return Optional.empty();
  }

  /**
   * Add a sensor to this node.
   *
   * @param sensor A sensor to add to this node.
   */
  public void addSensor (@NonNull final Sensor sensor) {
    if (!_sensors.contains(sensor)) {
      _sensors.add(sensor);
      sensor.setNode(this);
    }
  }

  public void addSensors (@NonNull final Iterable<@NonNull Sensor> sensors) {
    for (@NonNull final Sensor sensor : sensors) {
      addSensor(sensor);
    }
  }

  public void addSensors (@NonNull final Iterator<@NonNull Sensor> sensors) {
    while (sensors.hasNext()) {
      addSensor(sensors.next());
    }
  }

  public void addSensors (@NonNull final Sensor... sensors) {
    for (@NonNull final Sensor sensor : sensors) {
      addSensor(sensor);
    }
  }

  /**
   * Remove a sensor from this node.
   *
   * @param sensor A sensor to remove from this node.
   */
  public void removeSensor (@NonNull final Sensor sensor) {
    if (_sensors.contains(sensor)) {
      _sensors.remove(sensor);
      sensor.setNode(null);
    }
  }

  public void removeSensors (@NonNull final Iterable<@NonNull Sensor> sensors) {
    for (@NonNull final Sensor sensor : sensors) {
      removeSensor(sensor);
    }
  }

  public void removeSensors (@NonNull final Iterator<@NonNull Sensor> sensors) {
    while (sensors.hasNext()) {
      removeSensor(sensors.next());
    }
  }

  public void removeSensors (@NonNull final Sensor... sensors) {
    for (@NonNull final Sensor sensor : sensors) {
      removeSensor(sensor);
    }
  }

  public void removeAllSensors () {
    @NonNull final Iterator<Sensor> sensors = _sensors.iterator();
    while (sensors.hasNext()) { removeSensor(sensors.next()); }
  }

  public boolean hasSensor (@NonNull final Sensor sensor) {
    return _sensors.contains(sensor);
  }

  @Embedded
  public @NonNull NestedSetCoordinates getCoordinates () {
    return new NestedSetCoordinates(_coordinates);
  }

  public void setCoordinates (@Nullable final NestedSetCoordinates coordinates) {
    _coordinates = (coordinates == null) ? new NestedSetCoordinates() : new NestedSetCoordinates(coordinates);
  }

  @Override
  @Transient
  public ApplicationEntityReference<? extends Node> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  @NonNull
  public Node clone () {
    return new Node(this);
  }
}
