package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.SensorConfiguration;

/**
 * A database native sensor.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
public interface NativeSensorType
  extends SensorType
{
  @Override
  default @NonNull Class<? extends SensorConfiguration> getConfigurationClass () {
    return SensorConfiguration.class;
  }

  @Override
  default boolean isNative () {
    return true;
  }
}
