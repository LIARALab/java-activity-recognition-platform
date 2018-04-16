package org.liara.api.sensor;

import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class BaseSensorHandler implements SensorHandler
{  
  @Nullable
  private SensorHandlerManager _manager;
  
  @Nullable
  private Sensor _sensor;
  
  @Override
  public void attach (
    @NonNull final SensorHandlerManager manager, 
    @NonNull final Sensor sensor
  ) {
    if (manager != _manager || sensor != _sensor) {
      this.detach();
      willAttach(manager, sensor);
      
      _manager = manager;
      _sensor = sensor;
      
      if (manager != null) {
        manager.registerHandler(this);
      }
      
      didAttach();
    }
  }

  @Override
  public void detach () {
    if (_manager != null) {
      willDetach();
      final SensorHandlerManager oldManager = _manager;
      final Sensor oldSensor = _sensor;
      _manager = null;
      _sensor = null;
      oldManager.unregisterHandler(this);
      didDetach(oldManager, oldSensor);
    }
  }
  
  protected void willAttach (
    @NonNull final SensorHandlerManager manager,
    @NonNull final Sensor sensor
  ) { }
  
  protected void didAttach () { }
  
  protected void willDetach () { } 

  protected void didDetach (
    @NonNull final SensorHandlerManager manager,
    @NonNull final Sensor sensor
  ) { }
  
  @Override
  public SensorHandlerManager getManager () {
    return _manager;
  }

  @Override
  public Sensor getHandledSensor () {
    return _sensor;
  }
}
