package org.liara.api.recognition.condition;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class LtSensorConfiguration
  extends ConditionSensorConfiguration
{
  @Nullable
  private Double _ceil;

  public LtSensorConfiguration () {
    super();
    _ceil = null;
  }

  public LtSensorConfiguration (@NonNull final LtSensorConfiguration configuration) {
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
  LtSensorConfiguration clone () {
    return new LtSensorConfiguration(this);
  }
}
