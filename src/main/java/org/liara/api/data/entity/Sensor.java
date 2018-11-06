package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

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
  private String _type;

  @Nullable
  private String _unit;

  @Nullable
  private ApplicationEntityReference<? extends Node> _nodeIdentifier;

  @Nullable
  private Boolean _isVirtual;

  @Nullable
  private SensorConfiguration _configuration;

  public Sensor () {
    super();
    _name = null;
    _type = null;
    _unit = null;
    _nodeIdentifier = null;
    _configuration = null;
    _isVirtual = null;
  }

  public Sensor (@NonNull final Sensor toCopy) {
    super(toCopy);
    _name = toCopy.getName();
    _type = toCopy.getType();
    _unit = toCopy.getUnit();
    _configuration = toCopy.getConfiguration();
    _isVirtual = toCopy.isVirtual();
  }

  @Column(name = "name", nullable = false)
  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }

  @Column(name = "node_identifier", nullable = false)
  public @Nullable ApplicationEntityReference<? extends Node> getNodeIdentifier () {
    return _nodeIdentifier;
  }

  public void setNodeIdentifier (ApplicationEntityReference nodeIdentifier) {
    _nodeIdentifier = nodeIdentifier;
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
    return new Sensor(this);
  }
}

