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

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
@UseCreationSchema(StateCreationSchema.class)
@UseMutationSchema(StateMutationSchema.class)
public class State extends ApplicationEntity
{  
  @Column(name = "emitted_at", nullable = false, updatable = true, unique = false)
  private ZonedDateTime _emittionDate;
  
  @ManyToOne(optional = false)
  @JoinColumn(name = "sensor_identifier", nullable = false, unique = false, updatable = true)
  private Sensor _sensor;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
    name = "correlations_of_states",
    joinColumns = @JoinColumn(name = "target_identifier"),
    inverseJoinColumns = @JoinColumn(name = "correlated_identifier")
  )
  @MapKeyColumn(name = "label")
  private Map<String, State> _correlations = new HashMap<>();
  
  public State () { 
    _emittionDate = ZonedDateTime.now();
    _sensor = null;
  }
  
  public State (@NonNull final StateCreationSchema schema) {
    _sensor = EntityCollections.SENSORS.findByIdentifier(schema.getSensor()).get();
    _emittionDate = schema.getEmittionDate();
    
    for (final Map.Entry<String, Long> correlation : schema.correlations()) {
      correlate(
        correlation.getKey(), 
        EntityCollections.STATES.findByIdentifier(correlation.getValue()).get()
      );
    }
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  @JsonIgnore
  public Sensor getSensor () {
    return _sensor;
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
  
  public Iterable<Map.Entry<String, State>> correlations () {
    return Collections.unmodifiableSet(_correlations.entrySet());
  }
  
  public State getCorrelation (@NonNull final String label) {
    final String unifiedLabel = label.toLowerCase().trim();
    return _correlations.get(unifiedLabel);
  }

  @JsonIgnore
  public Set<Map.Entry<String, State>> getCorrelations () {
    return Collections.unmodifiableSet(_correlations.entrySet());
  }
  
  public boolean hasCorrelation (@NonNull final String label) {
    final String unifiedLabel = label.toLowerCase().trim();
    return _correlations.containsKey(unifiedLabel);
  }
  
  public boolean isCorrelated (@NonNull final State state) {
    return _correlations.containsValue(state);
  }
  
  public boolean isCorrelated (
    @NonNull final String label,
    @NonNull final State state
  ) {
    final String unifiedLabel = label.toLowerCase().trim();
    return Objects.equal(_correlations.get(unifiedLabel), state);
  }
  
  public void setSensor (@Nullable final Sensor sensor) {
    if (_sensor != sensor) {
      if (_sensor != null) {
        final Sensor oldSensor = _sensor;
        _sensor = null;
        oldSensor.removeState(this);
      }
      
      _sensor = sensor;
      
      if (_sensor != null) {
        _sensor.addState(this);
      }
    }
  }

  public Long getSensorIdentifier () {
    return _sensor == null ? null : _sensor.getIdentifier();
  }

  public void setEmittionDate (@NonNull final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }

  @JsonIgnore
  public Long getNodeIdentifier () {
    return _sensor.getNodeIdentifier();
  }
  
  @Override
  public StateSnapshot snapshot () {
    return new StateSnapshot(this);
  }
}
