package org.liara.api.event;

import org.liara.api.data.entity.Sensor;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class SensorWasCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 784473504749676317L;
  
  @NonNull
  private final Sensor _sensor;
  
  /**
   * 
   * @param source
   * @param sensor
   */
  public SensorWasCreatedEvent(
    @NonNull final Object source,
    @NonNull final Sensor sensor
  ) {
    super(source);
    _sensor = sensor.clone();
  }

  public Sensor getSensor () {
    return _sensor.clone();
  }
}
