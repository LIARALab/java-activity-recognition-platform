package org.liara.api.test.builder.sensor;

import groovy.lang.Closure;
import org.liara.api.data.entity.Sensor;
import org.liara.api.recognition.sensor.common.NativeBooleanSensor;
import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;

public class SensorBuilder extends BaseSensorBuilder<SensorBuilder, Sensor>
{
  public static SensorBuilder motion (@NonNull final Closure<?> closure) {
    final SensorBuilder result = new SensorBuilder();
    
    result.withType(NativeMotionSensor.class);
    Closures.callAs(closure, result);
    
    return result;
  }

  public static SensorBuilder nativeBoolean (@NonNull final Closure<?> closure) {
    final SensorBuilder result = new SensorBuilder();
    
    result.withType(NativeBooleanSensor.class);
    Closures.callAs(closure, result);
    
    return result;
  }
  
  public static SensorBuilder ofType (@NonNull final Class<?> type) {
    final SensorBuilder result = new SensorBuilder();
    result.withType(type);
    return result;
  }
  
  public static SensorBuilder ofType (
    @NonNull final Class<?> type, 
    @NonNull final Closure<?> closure
  ) {
    final SensorBuilder result = new SensorBuilder();
    
    result.withType(type);
    Closures.callAs(closure, result);
    
    return result;
  }
  
  @Override
  public SensorBuilder self () {
    return this;
  }

  @Override
  public Sensor build () {
    final Sensor result = new Sensor();
    super.apply(result);
    return result;
  }
}
