package org.liara.api.data.entity;

import org.springframework.lang.NonNull;

import java.security.InvalidParameterException;

public interface SensorConfiguration extends Cloneable
{
  default <T extends SensorConfiguration> T as (
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

  SensorConfiguration clone ();
}
