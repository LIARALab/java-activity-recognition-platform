package org.liara.api.test.builder.sensor;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.sensor.SensorCreationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.test.builder.state.StateBuilder;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import groovy.lang.Closure;

public abstract class SensorBuilder<Self extends SensorBuilder<Self>>
{
  public static MotionSensorBuilder createMotionSensor (
    @NonNull final Closure<?> closure
  ) {
    final MotionSensorBuilder builder = new MotionSensorBuilder();
    Closures.callAs(closure, builder);
    return builder;
  }
  
  public static MotionSensorBuilder createMotionSensor () {
    return new MotionSensorBuilder();
  }
  
  @NonNull
  private final String _type;
  
  @NonNull
  private String _name = "unnamed";
  
  @Nullable
  private String _valueUnit = null;
  
  @Nullable
  private String _valueLabel = null;
  
  @Nullable
  private String _ipv4Address = null;
  
  @Nullable
  private String _ipv6Address = null;
  
  @Nullable
  private Node _parent = null;
  
  @Nullable
  private SensorConfiguration _configuration = null;
  
  @NonNull
  private final List<StateBuilder<?>> _states = new ArrayList<>();
  
  public SensorBuilder (@NonNull final Class<?> type) {
    _type = type.getClass().getName();
  }
  
  public Self willBeBuild () {
    return self();
  }
  
  public Self withName (@NonNull final String name) {
    _name = name;
    return self();
  }
  
  public Self withValueUnit (@NonNull final String valueUnit) {
    _valueUnit = valueUnit;
    return self();
  }
  
  public Self withValueLabel (@NonNull final String valueLabel) {
    _valueLabel = valueLabel;
    return self();
  }
  
  public Self withIpv4Address (@NonNull final String ipv4Address) {
    _ipv4Address = ipv4Address;
    return self();
  }
  
  public Self withIpv6Address (@NonNull final String ipv6Address) {
    _ipv6Address = ipv6Address;
    return self();
  }
  
  public Self withParent (@NonNull final Node parent) {
    _parent = parent;
    return self();
  }
  
  public Self withConfiguration (@NonNull final SensorConfiguration configuration) {
    _configuration = configuration;
    return self();
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder withState (
    @NonNull final SubBuilder builder
  ) {
    _states.add(builder);
    return builder;
  }
  
  public <SubBuilder extends StateBuilder<?>> Self withState (
    @NonNull final SubBuilder builder,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    _states.add(builder);
    callback.apply(builder);
    return self();
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfter (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final TemporalUnit unit
  ) {
    if (_states.size() <= 0) {
      throw new IllegalStateException(String.join(
        "",
        "Unnable to add a state after the last added state of the sensor because ",
        "the sensor instance does not have any state registered in."
      ));
    }
    
    withState(builder);
    builder.withEmittionDate(_states.get(_states.size() - 1).getEmittionDate().plus(duration, unit));
    return builder;
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfter (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final TemporalUnit unit,
    @NonNull final Function<SubBuilder, Void> callback
    
  ) {
    if (_states.size() <= 0) {
      throw new IllegalStateException(String.join(
        "",
        "Unnable to add a state after the last added state of the sensor because ",
        "the sensor instance does not have any state registered in."
      ));
    }
    
    withState(builder);
    builder.withEmittionDate(_states.get(_states.size() - 1).getEmittionDate().plus(duration, unit));
    callback.apply(builder);
    return self();
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfter (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final TemporalUnit unit,
    @NonNull final Closure<?> callback
    
  ) {
    if (_states.size() <= 0) {
      throw new IllegalStateException(String.join(
        "",
        "Unnable to add a state after the last added state of the sensor because ",
        "the sensor instance does not have any state registered in."
      ));
    }
    
    withState(builder);
    builder.withEmittionDate(_states.get(_states.size() - 1).getEmittionDate().plus(duration, unit));
    Closures.callAs(callback, builder);
    return self();
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterMilliseconds (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MILLIS);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterMilliseconds (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
    
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MILLIS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterMilliseconds (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
    
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MILLIS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterSeconds (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.SECONDS);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterSeconds (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
    
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.SECONDS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterSeconds (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
    
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.SECONDS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterMinutes (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MINUTES);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterMinutes (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MINUTES, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterMinutes (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MINUTES, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterHours (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.HOURS);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterHours (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.HOURS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterHours (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.HOURS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterDays (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.DAYS);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterDays (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.DAYS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterDays (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.DAYS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterMonths (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MONTHS);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterMonths (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MONTHS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterMonths (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.MONTHS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> SubBuilder andWithStateAfterYears (
    @NonNull final SubBuilder builder,
    @NonNull final int duration
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.YEARS);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterYears (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.YEARS, callback);
  }
  
  public <SubBuilder extends StateBuilder<?>> Self andWithStateAfterYears (
    @NonNull final SubBuilder builder,
    @NonNull final int duration,
    @NonNull final Closure<?> callback
  ) {
    return andWithStateAfter(builder, duration, ChronoUnit.YEARS, callback);
  }
  
  public Sensor build (@NonNull final SchemaManager manager) {
    final SensorCreationSchema schema = new SensorCreationSchema();
    schema.setConfiguration(_configuration);
    schema.setIpv4Address(_ipv4Address);
    schema.setIpv6Address(_ipv6Address);
    schema.setValueLabel(_valueLabel);
    schema.setValueUnit(_valueUnit);
    schema.setName(_name);
    schema.setParent(_parent);
    schema.setType(_type);
    
    final Sensor sensor = manager.execute(schema);
    
    for (final StateBuilder<?> state : _states) {
      state.withSource(sensor);
      state.build(manager);
    }
    
    return sensor;
  }
  
  public abstract Self self ();
}
