package org.liara.api.test.builder.sensor;

import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;

import groovy.lang.Closure;

public class SensorBuilder extends BaseSensorBuilder<SensorBuilder>
{
  public static SensorBuilder motion (@NonNull final Closure<?> closure) {
    final SensorBuilder result = new SensorBuilder();
    
    result.withType(NativeMotionSensor.class);
    Closures.callAs(closure, result);
    
    return result;
  }
  
  @Override
  public SensorBuilder self () {
    return this;
  }
}
