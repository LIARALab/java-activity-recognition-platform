package org.liara.recognition.sensor;

import java.util.ArrayList;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

public class VirtualSensorRunner
{
  @NonNull
  private final Sensor _sensor;
  
  @NonNull
  private final VirtualSensorHandler _handler;
  
  @NonNull
  private final ArrayList<State> _buffer = new ArrayList<>(200);
  
  protected VirtualSensorRunner (
    @NonNull final Sensor sensor,
    @NonNull final VirtualSensorHandler handler
  ) {
    _sensor = sensor;
    _handler = handler;
  }
  
  public static VirtualSensorRunner create (@NonNull final Sensor sensor) {
    return null;
  }
  
  public static VirtualSensorRunner restart (@NonNull final Sensor sensor) {
    return null;
  }
  
  public void delete () {
    
  }
  
  public void stop () {
    
  }

  public Sensor getSensor () {
    return _sensor;
  }

  public VirtualSensorHandler getHandler () {
    return _handler;
  }
}
