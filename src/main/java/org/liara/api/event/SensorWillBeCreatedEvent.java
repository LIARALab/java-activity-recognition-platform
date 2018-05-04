package org.liara.api.event;

import org.liara.api.data.entity.sensor.SensorCreationSchema;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class SensorWillBeCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 363938627402557748L;
  
  @NonNull
  private final SensorCreationSchema _sensor;
  
  /**
   * 
   * @param source
   * @param sensor
   */
  public SensorWillBeCreatedEvent(
    @NonNull final Object source,
    @NonNull final SensorCreationSchema sensor
  ) {
    super(source);
    _sensor = sensor;
  }

  public SensorCreationSchema getSensor () {
    return _sensor;
  }
}
