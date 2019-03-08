package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.Tag;
import org.liara.api.data.entity.TagRelation;
import org.liara.api.relation.RelationFactory;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.joining.Join;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "state_correlations")
public class Correlation
  extends ApplicationEntity
{
  @RelationFactory(Tag.class)
  public static @NonNull Operator tags () {
    return ApplicationEntity.tags(Correlation.class);
  }

  public static @NonNull Operator tags (@NonNull final Correlation correlation) {
    return ApplicationEntity.tags(Correlation.class, correlation);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator tagRelations () {
    return ApplicationEntity.tagRelations(Correlation.class);
  }

  public static @NonNull Operator tagRelations (@NonNull final Correlation correlation) {
    return ApplicationEntity.tagRelations(Correlation.class, correlation);
  }

  @RelationFactory(State.class)
  public static @NonNull Operator start () {
    return Join.inner(State.class)
             .filter(Filter.expression(":this.identifier = :super.startStateIdentifier"));
  }

  public static @NonNull Operator start (@NonNull final Correlation correlation) {
    return Filter.expression(":this.identifier = :stateIdentifier")
             .setParameter("stateIdentifier", correlation.getStartStateIdentifier());
  }

  @RelationFactory(State.class)
  public static @NonNull Operator end () {
    return Join.inner(State.class)
             .filter(Filter.expression(":this.identifier = :super.endStateIdentifier"));
  }

  public static @NonNull Operator end (@NonNull final Correlation correlation) {
    return Filter.expression(":this.identifier = :stateIdentifier")
             .setParameter("stateIdentifier", correlation.getEndStateIdentifier());
  }

  @Nullable
  private Long _startStateIdentifier;

  @Nullable
  private Long _endStateIdentifier;

  @Nullable
  private String _name;

  public Correlation () {
    _startStateIdentifier = null;
    _endStateIdentifier = null;
    _name = null;
  }

  public Correlation (@NonNull final Correlation toCopy) {
    super(toCopy);
    _startStateIdentifier = toCopy.getStartStateIdentifier();
    _endStateIdentifier = toCopy.getEndStateIdentifier();
    _name = toCopy.getName();
  }

  @Column(name = "start_state_identifier", nullable = false)
  @ApplicationEntityReference(Sensor.class)
  public @Nullable Long getStartStateIdentifier () {
    return _startStateIdentifier;
  }

  public void setStartStateIdentifier (@Nullable final Long startStateIdentifier) {
    _startStateIdentifier = startStateIdentifier;
  }

  @Column(name = "end_state_identifier", nullable = false)
  @ApplicationEntityReference(Sensor.class)
  public @Nullable Long getEndStateIdentifier () {
    return _endStateIdentifier;
  }

  public void setEndStateIdentifier (@Nullable final Long endStateIdentifier) {
    _endStateIdentifier = endStateIdentifier;
  }

  @Column(name = "name", nullable = false)
  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }
}
