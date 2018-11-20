package org.liara.api.data.repository;

import org.liara.api.data.entity.state.ValueState;

public interface SapaRepositories
{
  interface Boolean
    extends ValueStateRepository<java.lang.Boolean, ValueState.Boolean> {}

  interface String
    extends ValueStateRepository<java.lang.String, ValueState.String> {}

  interface Float
    extends ValueStateRepository<java.lang.Float, ValueState.Float> {}

  interface Double
    extends ValueStateRepository<java.lang.Double, ValueState.Double> {}

  interface Byte
    extends ValueStateRepository<java.lang.Byte, ValueState.Byte> {}

  interface Short
    extends ValueStateRepository<java.lang.Short, ValueState.Short> {}

  interface Integer
    extends ValueStateRepository<java.lang.Integer, ValueState.Integer> {}

  interface Long
    extends ValueStateRepository<java.lang.Long, ValueState.Long> {}

  interface Numeric
    extends ValueStateRepository<java.lang.Number, ValueState.Numeric> {}
}
