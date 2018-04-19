package org.liara.api.filter.ast;

import org.liara.api.date.PartialZonedDateTime;
import org.springframework.lang.NonNull;

public class ValueFilterNode<Value> extends BaseFilterNode
{
  @NonNull
  private final Value _value;
  
  public static ValueFilterNode<Integer> from (final int value) {
    return new ValueFilterNode<Integer>(CommonFilterNodeType.VALUE_INTEGER, value);
  }
  
  public static ValueFilterNode<Integer> from (@NonNull final Integer value) {
    return new ValueFilterNode<Integer>(CommonFilterNodeType.VALUE_INTEGER, value);
  }
  
  public static ValueFilterNode<Long> from (final long value) {
    return new ValueFilterNode<Long>(CommonFilterNodeType.VALUE_LONG, value);
  }
  
  public static ValueFilterNode<Long> from (@NonNull final Long value) {
    return new ValueFilterNode<Long>(CommonFilterNodeType.VALUE_LONG, value);
  }
  
  public static ValueFilterNode<Float> from (final float value) {
    return new ValueFilterNode<Float>(CommonFilterNodeType.VALUE_FLOAT, value);
  }
  
  public static ValueFilterNode<Float> from (@NonNull final Float value) {
    return new ValueFilterNode<Float>(CommonFilterNodeType.VALUE_FLOAT, value);
  }
  
  public static ValueFilterNode<Double> from (final double value) {
    return new ValueFilterNode<Double>(CommonFilterNodeType.VALUE_FLOAT, value);
  }
  
  public static ValueFilterNode<Double> from (@NonNull final Double value) {
    return new ValueFilterNode<Double>(CommonFilterNodeType.VALUE_DOUBLE, value);
  }
  
  public static ValueFilterNode<PartialZonedDateTime> from (@NonNull final PartialZonedDateTime value) {
    return new ValueFilterNode<PartialZonedDateTime>(CommonFilterNodeType.VALUE_DATETIME, value);
  }
  
  public static ValueFilterNode<String> from (@NonNull final String value) {
    return new ValueFilterNode<String>(CommonFilterNodeType.VALUE_STRING, value);
  }

  private ValueFilterNode(@NonNull final FilterNodeType type, @NonNull final Value value) {
    super(type);
    _value = value;
  }

  public Value getValue () {
    return _value;
  }
}
