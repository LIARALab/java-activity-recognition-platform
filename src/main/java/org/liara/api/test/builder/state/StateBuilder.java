package org.liara.api.test.builder.state;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateCreationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class StateBuilder<Self extends StateBuilder<Self>>
{
  public static BooleanStateBuilder createBoolean () {
    return new BooleanStateBuilder();
  }
  
  @NonNull
  private static final DateTimeFormatter DEFAULT_FORMATTER = new DateTimeFormatterBuilder().appendPattern(
    "d-M-y[ H:m:s.S]"
  ).parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
   .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
   .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
   .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
   .toFormatter();
  
  @Nullable
  private ZonedDateTime _emittionDate = null;
  
  @Nullable
  private Sensor _sensor = null;
  
  public Self willBeBuild () {
    return self();
  }
  
  public Self withSource (@Nullable final Sensor source) {
    _sensor = source;
    return self();
  }
  
  public Self withEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
    
    return self();
  }
  
  public Self withEmittionDate (@NonNull final String string) {
    _emittionDate = ZonedDateTime.parse(
      string, 
      StateBuilder.DEFAULT_FORMATTER
    );
    
    return self();
  }
  
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }

  public Sensor getSource () {
    return _sensor;
  }
  
  public StateCreationSchema buildSchema () {
    return apply(new StateCreationSchema());
  }
  
  protected StateCreationSchema apply (@NonNull final StateCreationSchema schema) {
    schema.setEmittionDate(_emittionDate);
    schema.setSensor(_sensor);
    return schema;
  }
  
  public State build () {
    return buildSchema().create();
  }

  public State build (@NonNull final SchemaManager manager) {    
    return manager.execute(buildSchema());
  }
  
  public abstract Self self ();
}
