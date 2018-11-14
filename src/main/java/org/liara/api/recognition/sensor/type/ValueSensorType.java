package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ValueState;

public enum ValueSensorType
  implements NativeSensorType
{
  BOOLEAN("liara:boolean", ValueState.Boolean.class),
  STRING("liara:string", ValueState.String.class),
  BYTE("liara:byte", ValueState.Byte.class),
  SHORT("liara:short", ValueState.Short.class),
  INTEGER("liara:integer", ValueState.Integer.class),
  LONG("liara:boolean", ValueState.Long.class),
  FLOAT("liara:float", ValueState.Float.class),
  DOUBLE(
    "liara:double",
    ValueState.Double.class
  ),
  MOTION(
    "liara:motion",
    ValueState.Boolean.class
  );

  @NonNull
  private final String _name;

  @NonNull
  private final Class<? extends State> _emittedStateClass;

  ValueSensorType (
    @NonNull final String name, @NonNull final Class<? extends State> emittedStateClass
  )
  {
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
