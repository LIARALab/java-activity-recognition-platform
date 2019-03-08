package org.liara.api.data.repository;

import org.liara.api.data.entity.state.*;

public interface SapaRepositories
{
  interface Boolean
    extends ValueStateRepository<java.lang.Boolean, BooleanValueState> {}

  interface String
    extends ValueStateRepository<java.lang.String, StringValueState> {}

  interface Float
    extends ValueStateRepository<java.lang.Float, FloatValueState> {}

  interface Double
    extends ValueStateRepository<java.lang.Double, DoubleValueState> {}

  interface Byte
    extends ValueStateRepository<java.lang.Byte, ByteValueState> {}

  interface Short
    extends ValueStateRepository<java.lang.Short, ShortValueState> {}

  interface Integer
    extends ValueStateRepository<java.lang.Integer, IntegerValueState> {}

  interface Long
    extends ValueStateRepository<java.lang.Long, LongValueState> {}

  interface Numeric
    extends ValueStateRepository<java.lang.Number, NumericValueState> {}

  interface State
    extends StateRepository<org.liara.api.data.entity.state.State> {}
}
