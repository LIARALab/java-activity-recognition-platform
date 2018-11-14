package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.state.State;

public interface SensorType
{
  @NonNull Class<? extends State> getEmittedStateClass ();

  @NonNull Class<? extends SensorConfiguration> getConfigurationClass ();

  boolean isNative ();

  @NonNull String getName ();
}
