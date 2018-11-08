package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.InvalidParameterException;

public interface SensorConfiguration
{
  default <T extends SensorConfiguration> @NonNull T as (
    @NonNull final Class<T> clazz
  ) {
    if (clazz.isAssignableFrom(getClass())) {
      return clazz.cast(this);
    } else {
      throw new InvalidParameterException(
        String.join(
          "",
          "A sensor configuration of type ", getClass().toString(),
          " can't be cast to ",
          clazz.toString(), "."
        )
      );
    }
  }
}
