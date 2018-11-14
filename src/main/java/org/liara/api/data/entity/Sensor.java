package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.recognition.sensor.type.SensorType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "sensors")
@JsonPropertyOrder({ "identifier", "name", "type", "node_identifier", "configuration" })
public class Sensor
  extends ApplicationEntity
{
  @Nullable
  private String _name;

  @Nullable
  private SensorType _type;

  @Nullable
  private String _unit;

  @Nullable
  private Long _nodeIdentifier;

  @Nullable
  private SensorConfiguration _configuration;

  public Sensor () {
    super();
    _name = null;
    _type = null;
    _unit = null;
    _nodeIdentifier = null;
    _configuration = null;
  }

  public Sensor (@NonNull final Sensor toCopy) {
    super(toCopy);
    _name = toCopy.getName();
    _type = toCopy.getType();
    _unit = toCopy.getUnit();
    _nodeIdentifier = toCopy.getNodeIdentifier();
    _configuration = toCopy.getConfiguration();
  }

  @Column(name = "name", nullable = false)
  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }

  @Column(name = "node_identifier", nullable = false)
  public @Nullable Long getNodeIdentifier () {
    return _nodeIdentifier;
  }

  public void setNodeIdentifier (@Nullable final Long nodeIdentifier) {
    _nodeIdentifier = nodeIdentifier;
  }

  @Column(name = "type", nullable = false, updatable = true, unique = false)
  public @Nullable SensorType getType () {
    return _type;
  }

  public void setType (@Nullable final SensorType type) {
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
}

