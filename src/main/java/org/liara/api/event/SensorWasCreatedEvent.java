package org.liara.api.event;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.sensor.SensorSnapshot;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class SensorWasCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 784473504749676317L;
  
  @NonNull
  private final SensorSnapshot _sensor;
  
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
    _sensor = sensor.snapshot();
  }

  public SensorSnapshot getSensor () {
    return _sensor;
  }
}
