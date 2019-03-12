package org.liara.api.recognition.condition;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class GtSensorConfiguration
  extends ConditionSensorConfiguration
{
  @Nullable
  private Double _floor;

  public GtSensorConfiguration () {
    super();
    _floor = null;
  }

  public GtSensorConfiguration (@NonNull final GtSensorConfiguration configuration) {
    super(configuration);
    _floor = configuration.getFloor();
  }


  public @Nullable
  Double getFloor () {
    return _floor;
  }

  public void setFloor (@Nullable final Double floor) {
    _floor = floor;
  }

  @Override
  public @NonNull
  GtSensorConfiguration clone () {
    return new GtSensorConfiguration(this);
  }
}
