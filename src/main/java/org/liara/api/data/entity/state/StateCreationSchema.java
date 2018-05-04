package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(State.class)
@JsonDeserialize(using = StateCreationSchemaDeserializer.class)
public class StateCreationSchema
{
  @Nullable
  private ZonedDateTime _emittionDate = null;
  
  @Nullable
  private Long _sensor = null;
  
  @Required
  @IdentifierOfEntityInCollection(collection = SensorCollection.class)
  public Long getSensor () {
    return _sensor;
  }
  
  @JsonSetter
  public void setSensor (@Nullable final Long sensor) {
    _sensor = sensor;
  }
  
  public void setSensor (@Nullable final Sensor sensor) {
    if (sensor == null) {
      _sensor = null;
    } else {
      _sensor = sensor.getIdentifier();
    }
  }
  
  public void setSensor (@NonNull final Optional<Long> sensor) {
    _sensor = sensor.orElse(null);
  }
  
  @Required
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  @JsonSetter
  public void setEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }
  
  public void setEmittionDate (@NonNull final Optional<ZonedDateTime> emittionDate) {
    _emittionDate = emittionDate.orElse(null);
  }
  
  public State create () {
    return new State(this);
  }
}
