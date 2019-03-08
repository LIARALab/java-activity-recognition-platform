package org.liara.api.recognition.sensor.common.virtual.condition;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.recognition.sensor.type.ComputedSensorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GtSensor
  extends ConditionSensor
  implements ComputedSensorType
{
  @Autowired
  public GtSensor (@NonNull final ConditionSensorBuilder builder) { super(builder); }

  @Override
  protected boolean check (@NonNull final State state) {
    @Nullable final ValueState value = asValueState(state);

    return Number.class.isAssignableFrom(value.getType()) &&
           ((Number) value.getValue()).doubleValue() > getFloor();
  }

  private double getFloor () {
    return getConfiguration(GtSensorConfiguration.class).map(GtSensorConfiguration::getFloor)
             .orElseThrow();
  }

  private @Nullable ValueState asValueState (@NonNull final State state) {
    return state instanceof ValueState ? ((ValueState) state) : null;
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return BooleanValueState.class;
  }

  @Override
  public @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return GtSensorConfiguration.class;
  }

  @Override
  public @NonNull String getName () {
    return "liara:greaterthan";
  }
}
