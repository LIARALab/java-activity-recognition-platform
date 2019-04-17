package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.*;

public enum ValueSensorType
  implements NativeSensorType
{
  BOOLEAN("liara:boolean", BooleanValueState.class),
  STRING("liara:string", StringValueState.class),
  BYTE("liara:byte", ByteValueState.class),
  SHORT("liara:short", ShortValueState.class),
  INTEGER("liara:integer", IntegerValueState.class),
  LONG("liara:long", LongValueState.class),
  FLOAT("liara:float", FloatValueState.class),
  DOUBLE("liara:double", DoubleValueState.class),
  MOTION("liara:motion", BooleanValueState.class);

  @NonNull
  private final String _name;

  @NonNull
  private final Class<? extends State> _emittedStateClass;

  ValueSensorType (
    @NonNull final String name,
    @NonNull final Class<? extends State> emittedStateClass
  ) {
    _name = name;
    _emittedStateClass = emittedStateClass;
  }

  @Override
  public @NonNull Class<? extends State> getEmittedStateClass () {
    return _emittedStateClass;
  }

  @Override
  public @NonNull String getName () {
    return _name;
  }
}
