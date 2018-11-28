package org.liara.api.recognition.sensor.common.virtual.condition;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class GtSensorConfiguration
  extends ConditionSensorConfiguration
{
  @Nullable
  private Double _ceil;

  public GtSensorConfiguration () {
    super();
    _ceil = null;
  }

  public GtSensorConfiguration (@NonNull final GtSensorConfiguration configuration) {
    super(configuration);
    _ceil = configuration.getCeil();
  }

  @Nullable
  public Double getCeil () {
    return _ceil;
  }

  public void setCeil (@Nullable final Double ceil) {
    _ceil = ceil;
  }

  @Override
  public @NonNull
  GtSensorConfiguration clone () {
    return new GtSensorConfiguration(this);
  }
}
