package org.liara.api.sensor;

import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;

public interface SensorHandler
{
  public void attach (
    @NonNull final SensorHandlerManager manager,
    @NonNull final Sensor sensor
  );
  
  public void detach ();
  
  public default boolean isAttached () {
    return getManager() != null;
  }
  
  public default boolean isDetached () {
    return getManager() == null;
  }
  
  public SensorHandlerManager getManager ();
  
  public Sensor getHandledSensor ();
}
