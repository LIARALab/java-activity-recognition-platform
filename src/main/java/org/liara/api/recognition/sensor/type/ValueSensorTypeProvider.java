package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.SensorType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public final class ValueSensorTypeProvider
{
  @Bean("value_sensor_type_motion")
  public static @NonNull SensorType createLiaraMotionSensorType () {
    return ValueSensorType.MOTION;
  }

  @Bean("value_sensor_type_boolean")
  public static @NonNull SensorType createLiaraBooleanSensorType () {
    return ValueSensorType.BOOLEAN;
  }

  @Bean("value_sensor_type_string")
  public static @NonNull SensorType createLiaraStringSensorType () {
    return ValueSensorType.STRING;
  }

  @Bean("value_sensor_type_byte")
  public static @NonNull SensorType createLiaraByteSensorType () {
    return ValueSensorType.BYTE;
  }

  @Bean("value_sensor_type_short")
  public static @NonNull SensorType createLiaraShortSensorType () {
    return ValueSensorType.SHORT;
  }

  @Bean("value_sensor_type_integer")
  public static @NonNull SensorType createLiaraIntegerSensorType () {
    return ValueSensorType.INTEGER;
  }

  @Bean("value_sensor_type_long")
  public static @NonNull SensorType createLiaraLongSensorType () {
    return ValueSensorType.LONG;
  }

  @Bean("value_sensor_type_float")
  public static @NonNull SensorType createLiaraFloatSensorType () {
    return ValueSensorType.FLOAT;
  }

  @Bean("value_sensor_type_double")
  public static @NonNull SensorType createLiaraDoubleSensorType () {
    return ValueSensorType.DOUBLE;
  }
}
