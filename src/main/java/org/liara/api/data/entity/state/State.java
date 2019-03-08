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
package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.Tag;
import org.liara.api.data.entity.TagRelation;
import org.liara.api.relation.RelationFactory;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.joining.Join;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
public class State extends ApplicationEntity
{
  @RelationFactory(Tag.class)
  public static @NonNull Operator tags () {
    return ApplicationEntity.tags(State.class);
  }

  public static @NonNull Operator tags (@NonNull final State state) {
    return ApplicationEntity.tags(State.class, state);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator tagRelations () {
    return ApplicationEntity.tagRelations(State.class);
  }

  public static @NonNull Operator tagRelations (@NonNull final State state) {
    return ApplicationEntity.tagRelations(State.class, state);
  }

  @RelationFactory(Sensor.class)
  public static @NonNull Operator sensor () {
    return Join.inner(Sensor.class).filter(
      Filter.expression(":this.identifier = :super.sensorIdentifier")
    );
  }

  public static @NonNull Operator sensor (@NonNull final State state) {
    return Filter.expression(":this.identifier = :identifier")
             .setParameter("identifier", state.getSensorIdentifier());
  }

  @RelationFactory(Correlation.class)
  public static @NonNull Operator correlations () {
    return Composition.of(
      Filter.expression(":this.startStateIdentifier = :super.identifier"),
      Filter.expression(":this.endStateIdentifier = :super.identifier")
    );
  }

  public static @NonNull Operator correlations (@NonNull final State state) {
    return Composition.of(
      Filter.expression(":this.startStateIdentifier = :identifier")
        .setParameter("identifier", state.getIdentifier()),
      Filter.expression(":this.endStateIdentifier = :identifier")
        .setParameter("identifier", state.getIdentifier())
    );
  }

  @RelationFactory(Correlation.class)
  public static @NonNull Operator correlationsTo () {
    return Composition.of(
      Filter.expression(":this.endStateIdentifier = :super.identifier")
    );
  }

  public static @NonNull Operator correlationsTo (@NonNull final State state) {
    return Composition.of(
      Filter.expression(":this.endStateIdentifier = :identifier")
        .setParameter("identifier", state.getIdentifier())
    );
  }

  @RelationFactory(Correlation.class)
  public static @NonNull Operator correlationsFrom () {
    return Composition.of(
      Filter.expression(":this.startStateIdentifier = :super.identifier")
    );
  }

  public static @NonNull Operator correlationsFrom (@NonNull final State state) {
    return Composition.of(
      Filter.expression(":this.startStateIdentifier = :identifier")
        .setParameter("identifier", state.getIdentifier())
    );
  }

  @Nullable
  private ZonedDateTime _emissionDate;

  @Nullable
  private Long _sensorIdentifier;
  
  public State () {
    _emissionDate = null;
    _sensorIdentifier = null;
  }

  public State (@NonNull final State toCopy) {
    super(toCopy);
    _emissionDate = toCopy.getEmissionDate();
    _sensorIdentifier = toCopy.getSensorIdentifier();
  }

  @Column(
    name = "sensor_identifier",
    nullable = false
  )
  @ApplicationEntityReference(Sensor.class)
  public @Nullable Long getSensorIdentifier () {
    return _sensorIdentifier;
  }

  public void setSensorIdentifier (@Nullable final Long sensorIdentifier) {
    _sensorIdentifier = sensorIdentifier;
  }

  @Column(
    name = "emitted_at",
    nullable = false,
    columnDefinition = "DATETIME(6)"
  )
  public @Nullable ZonedDateTime getEmissionDate () {
    return _emissionDate;
  }

  public void setEmissionDate (@Nullable final ZonedDateTime emissionDate) {
    _emissionDate = emissionDate;
  }
}
