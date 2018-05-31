package org.liara.api.test.builder.sensor;

import java.time.ZonedDateTime;

import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.liara.api.test.builder.state.BooleanStateBuilder;
import org.liara.api.test.builder.state.StateBuilder;
import org.springframework.lang.NonNull;

public class MotionSensorBuilder extends SensorBuilder<MotionSensorBuilder>
{
  public MotionSensorBuilder() {
    super(NativeMotionSensor.class);
  }
  
  public MotionSensorBuilder withStateAt (
    @NonNull final String emittionDate,
    @NonNull final boolean value
  ) {
    final BooleanStateBuilder builder = StateBuilder.createBoolean();
    builder.withEmittionDate(emittionDate).withValue(value);
    withState(builder);
    return this;
  }
  
  public MotionSensorBuilder withStateAt (
    @NonNull final ZonedDateTime emittionDate,
    @NonNull final boolean value
  ) {
    final BooleanStateBuilder builder = StateBuilder.createBoolean();
    builder.withEmittionDate(emittionDate).withValue(value);
    withState(builder);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterMilliseconds (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterMilliseconds(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterSeconds (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterSeconds(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterMinutes (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterMinutes(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterHours (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterHours(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterDays (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterDays(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterMonths (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterMonths(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }
  
  public MotionSensorBuilder andWithStateAfterYears (
    @NonNull final int duration,
    @NonNull final boolean value
  ) {
    andWithStateAfterYears(StateBuilder.createBoolean(), duration).withValue(value);
    return this;
  }

  @Override
  public MotionSensorBuilder self () {
    return this;
  }
}
