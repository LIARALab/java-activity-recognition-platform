package org.liara.api.recognition.sensor;

import java.security.InvalidParameterException;

import org.springframework.lang.NonNull;

public interface SensorConfiguration extends Cloneable
{
  public default <T extends SensorConfiguration> T as (
    @NonNull final Class<T> clazz
  ) {
    if (clazz.isAssignableFrom(this.getClass())) {
      return clazz.cast(this);
    } else {
      throw new InvalidParameterException(
        String.join(
          "", 
          "A sensor configuration of type ",
          this.getClass().toString(),
          " can't be cast to ",
          clazz.toString(), "."
        )
      );
    }
  }

  public SensorConfiguration clone ();
}
