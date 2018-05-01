package org.liara.api.event;

import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class SensorWillBeCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 363938627402557748L;
  
  @NonNull
  private final Sensor _sensor;
  
  /**
   * 
   * @param source
   * @param sensor
   */
  public SensorWillBeCreatedEvent(
    @NonNull final Object source,
    @NonNull final Sensor sensor
  ) {
    super(source);
    _sensor = sensor;
  }

  public Sensor getSensor () {
    return _sensor;
  }
}
