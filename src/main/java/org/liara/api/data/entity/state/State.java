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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
public class State extends ApplicationEntity
{
  @Nullable
  private ZonedDateTime _emittionDate;

  @Nullable
  private Sensor _sensor;

  @NonNull
  private Map<String, State> _correlations;
  
  public State () { 
    _emittionDate = null;
    _sensor = null;
    _correlations = new HashMap<>();
  }

  public State (@NonNull final State toCopy) {
    _emittionDate = toCopy.getEmittionDate();
    _sensor = toCopy.getSensor();
    _correlations = new HashMap<>(toCopy.getCorrelations());
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  @Column(name = "emitted_at", nullable = false, updatable = true, unique = false)
  public @Nullable ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }

  public void setEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }
  
  @JsonIgnore
  @ManyToOne(optional = false)
  @JoinColumn(name = "sensor_identifier", nullable = false, unique = false, updatable = true)
  public @Nullable Sensor getSensor () {
    return _sensor;
  }

  public void setSensor (@Nullable final Sensor sensor) {
    if (_sensor != sensor) {
      if (_sensor != null) {
        @NonNull final Sensor oldSensor = _sensor;
        _sensor = null;
        oldSensor.removeState(this);
      }

      _sensor = sensor;

      if (_sensor != null) {
        _sensor.addState(this);
      }
    }
  }

  public void correlate (
    @NonNull final String label, 
    @NonNull final State state
  ) {
    final String unifiedLabel = label.toLowerCase().trim();
    _correlations.put(unifiedLabel, state);
  }
  
  public void decorrelate (
    @NonNull final String label
  ) {
    final String unifiedLabel = label.toLowerCase().trim();
    _correlations.remove(unifiedLabel);
  }

  @Transient
  public @NonNull Optional<State> getCorrelation (@NonNull final String label) {
    final String unifiedLabel = label.toLowerCase().trim();
    return Optional.ofNullable(_correlations.getOrDefault(unifiedLabel, null));
  }

  @JsonIgnore
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "correlations_of_states", joinColumns = @JoinColumn(name = "master_identifier"),
    inverseJoinColumns = @JoinColumn(name = "slave_identifier"))
  @MapKeyColumn(name = "label")
  public @NonNull Map<String, State> getCorrelations () {
    return Collections.unmodifiableMap(_correlations);
  }

  public void setCorrelations (@Nullable final Map<@NonNull String, @NonNull State> correlations) {
    removeAllCorrelations();
    if (correlations != null) addCorrelations(correlations);
  }

  private void addCorrelations (@NonNull final Map<@NonNull String, @NonNull State> correlations) {
    for (final Map.@NonNull Entry<@NonNull String, @NonNull State> entry : correlations.entrySet()) {
      correlate(entry.getKey(), entry.getValue());
    }
  }

  public void removeAllCorrelations () {
    while (_correlations.size() > 0) { decorrelate(_correlations.keySet().iterator().next()); }
  }

  public boolean hasCorrelation (@NonNull final String label) {
    final String unifiedLabel = label.toLowerCase().trim();
    return _correlations.containsKey(unifiedLabel);
  }

  @Transient
  public boolean isCorrelated (@NonNull final State state) {
    return _correlations.containsValue(state);
  }

  @Transient
  public boolean isCorrelated (
    @NonNull final String label,
    @NonNull final State state
  ) {
    final String unifiedLabel = label.toLowerCase().trim();
    return Objects.equal(_correlations.get(unifiedLabel), state);
  }
  
  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends State> getReference () {
    return ApplicationEntityReference.of(this);
  }

  @Override
  public @NonNull State clone ()
  { return new State(this); }
}
