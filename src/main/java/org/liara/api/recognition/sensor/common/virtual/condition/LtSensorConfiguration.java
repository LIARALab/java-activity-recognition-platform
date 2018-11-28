package org.liara.api.recognition.sensor.common.virtual.condition;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class LtSensorConfiguration
  extends ConditionSensorConfiguration
{
  @Nullable
  private Double _floor;

  public LtSensorConfiguration () {
    super();
    _floor = null;
  }

  public LtSensorConfiguration (@NonNull final LtSensorConfiguration configuration) {
    super(configuration);
    _floor = configuration.getFloor();
  }

  @Nullable
  public Double getFloor () {
    return _floor;
  }

  public void setFloor (@Nullable final Double floor) {
    _floor = floor;
  }

  @Override
  public @NonNull
  LtSensorConfiguration clone () {
    return new LtSensorConfiguration(this);
  }
}
