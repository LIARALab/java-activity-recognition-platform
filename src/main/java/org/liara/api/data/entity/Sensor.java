package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.io.SensorConfigurationConverter;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.type.SensorTypeManagerFactory;
import org.liara.api.relation.RelationFactory;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.joining.InnerJoin;
import org.liara.collection.operator.joining.Join;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sensors")
@JsonPropertyOrder({ "identifier", "name", "type", "node_identifier", "configuration" })
public class Sensor
  extends ApplicationEntity
{
  @RelationFactory(Tag.class)
  public static @NonNull Operator tags () {
    return ApplicationEntity.tags(Sensor.class);
  }

  public static @NonNull Operator tags (@NonNull final Sensor sensor) {
    return ApplicationEntity.tags(Sensor.class, sensor);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator tagRelations () {
    return ApplicationEntity.tagRelations(Sensor.class);
  }

  public static @NonNull Operator tagRelations (@NonNull final Sensor sensor) {
    return ApplicationEntity.tagRelations(Sensor.class, sensor);
  }

  @Nullable
  private String _type;

  public static @NonNull Operator node (@NonNull final Sensor sensor) {
    return Filter.expression(":this.identifier = :nodeIdentifier")
             .setParameter("nodeIdentifier", sensor.getNodeIdentifier());
  }

  @RelationFactory(State.class)
  public static @NonNull Operator states () {
    return Filter.expression(":this.sensorIdentifier = :super.identifier");
  }

  public static @NonNull Operator states (@NonNull final Sensor sensor) {
    return Filter.expression(":this.sensorIdentifier = :sensorIdentifier")
             .setParameter("sensorIdentifier", sensor.getIdentifier());
  }

  @Nullable
  private String _name;

  @RelationFactory(Node.class)
  public static @NonNull InnerJoin<Node> node () {
    return Join.inner(Node.class).filter(
      Filter.expression(":this.identifier = :super.nodeIdentifier")
    );
  }

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
  @ApplicationEntityReference(Node.class)
  public @Nullable Long getNodeIdentifier () {
    return _nodeIdentifier;
  }

  public void setNodeIdentifier (@Nullable final Long nodeIdentifier) {
    _nodeIdentifier = nodeIdentifier;
  }

  @Column(name = "type", nullable = false, updatable = true, unique = false)
  public @Nullable String getType () {
    return _type;
  }

  public void setType (@Nullable final String type) {
    _type = type;
  }

  @Transient
  @JsonIgnore
  public @Nullable SensorType getTypeInstance () {
    if (_type == null) {
      return null;
    } else {
      return Objects.requireNonNull(SensorTypeManagerFactory.INSTANCE).getObject().get(_type);
    }
  }

  @Column(name = "unit", nullable = true, updatable = true, unique = false)
  public @Nullable String getUnit () {
    return _unit;
  }

  public void setUnit (@Nullable final String unit) {
    _unit = unit;
  }

  @Column(name = "configuration", nullable = true, updatable = false, unique = false)
  @Convert(converter = SensorConfigurationConverter.class)
  public @Nullable SensorConfiguration getConfiguration () {
    return _configuration;
  }

  public void setConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
  }
}

