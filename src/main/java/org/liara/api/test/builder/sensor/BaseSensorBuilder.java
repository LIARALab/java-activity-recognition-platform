package org.liara.api.test.builder.sensor;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.test.builder.entity.BaseApplicationEntityBuilder;
import org.liara.api.test.builder.state.BaseStateBuilder;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import groovy.lang.Closure;

public abstract class BaseSensorBuilder<
                        Self extends BaseSensorBuilder<Self, Entity>,
                        Entity extends Sensor
                      >
                extends BaseApplicationEntityBuilder<Self, Entity>
{
  public static SensorBuilder createMotionSensor (
    @NonNull final Closure<?> closure
  ) {
    final SensorBuilder builder = new SensorBuilder();
    Closures.callAs(closure, builder);
    return builder;
  }
  
  public static SensorBuilder createMotionSensor () {
    return new SensorBuilder();
  }
  
  @Nullable
  private BaseStateBuilder<?, ? extends State> _beforeLastDefinedState = null;
  
  @Nullable
  private BaseStateBuilder<?, ? extends State> _lastDefinedState = null;
  
  @Nullable
  private String _type = null;
  
  @Nullable
  private String _name = null;
  
  @Nullable
  private String _unit = null;
  
  @Nullable
  private SensorConfiguration _configuration = null;
  
  @NonNull
  private final Set<BaseStateBuilder<?, ? extends State>> _states = new HashSet<>();
  
  public Self withType (@Nullable final Class<?> type) {
    _type = type.getName();
    return self();
  }
  
  public Self withName (@Nullable final String name) {
    _name = name;
    return self();
  }
  
  public Self withUnit (@Nullable final String unit) {
    _unit = unit;
    return self();
  }
  
  public Self withConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
    return self();
  }
  
  public <SubBuilder extends BaseStateBuilder<?, ? extends State>> Self with (
    @NonNull final SubBuilder builder
  ) {
    _states.add(builder);
    _beforeLastDefinedState = _lastDefinedState;
    _lastDefinedState = builder;
    return self();
  }
  
  public <SubBuilder extends BaseStateBuilder<?, ? extends State>> Self andWith (
    @NonNull final SubBuilder builder
  ) {
    return with(builder);
  }
  
  public Self after (
    final int duration,
    @NonNull final TemporalUnit unit
  ) {
    if (_beforeLastDefinedState == null) {
      throw new IllegalStateException(String.join(
        "",
        "Invalid invocation of after : the current sensor builder ",
        "does not have any previous state registered in."
      ));
    }
    
    _lastDefinedState.withEmittionDate(
      _beforeLastDefinedState.getEmittionDate().plus(duration, unit)
    );
    
    return self();
  }
  
  public Self afterMilliseconds (final int duration) {
    return after(duration, ChronoUnit.MILLIS);
  }
  
  public Self afterSeconds (final int duration) {
    return after(duration, ChronoUnit.SECONDS);
  }
  
  public Self afterMinutes (final int duration) {
    return after(duration, ChronoUnit.MINUTES);
  }
  
  public Self afterHours (final int duration) {
    return after(duration, ChronoUnit.HOURS);
  }
  
  public Self afterDays (final int duration) {
    return after(duration, ChronoUnit.DAYS);
  }
  
  public Self afterMonths (final int duration) {
    return after(duration, ChronoUnit.MONTHS);
  }
  
  public Self afterYears (final int duration) {
    return after(duration, ChronoUnit.YEARS);
  }
  
  public Self at (@NonNull final ZonedDateTime date) {
    if (_lastDefinedState == null) {
      throw new IllegalStateException(String.join(
        "",
        "Invalid call to at : the current builder instance does not ",
        "have any state added yet."
      ));
    }
    
    _lastDefinedState.withEmittionDate(date);
    
    return self();
  }
  
  public void apply (@NonNull final Entity sensor) {    
    super.apply(sensor);
    
    sensor.setName(_name);
    sensor.setType(_type);
    sensor.setUnit(_unit);
    sensor.setConfiguration(_configuration);
    
    for (final BaseStateBuilder<?, ? extends State> state : _states) {
      sensor.addState(state.build());
    }
  }
}
