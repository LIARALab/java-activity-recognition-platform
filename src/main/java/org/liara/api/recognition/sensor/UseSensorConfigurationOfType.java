package org.liara.api.recognition.sensor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface UseSensorConfigurationOfType
{
  public Class<? extends SensorConfiguration> value ();
}
