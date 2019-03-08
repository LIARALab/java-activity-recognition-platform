package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NumericValueState<NumericValue extends Number>
  extends ValueState<NumericValue>
{
  public NumericValueState (@NonNull final Class<NumericValue> type) {
    super(type);
  }

  public NumericValueState (final @NonNull NumericValueState<NumericValue> toCopy) {
    super(toCopy);
  }
}
