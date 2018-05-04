/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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

  public static ValueFilterNode<Boolean> from (final boolean b) {
    return new ValueFilterNode<Boolean>(CommonFilterNodeType.VALUE_BOOLEAN, b);
  }
  
  public static ValueFilterNode<Boolean> from (@NonNull final Boolean b) {
    return new ValueFilterNode<Boolean>(CommonFilterNodeType.VALUE_BOOLEAN, b);
  }

  private ValueFilterNode(@NonNull final FilterNodeType type, @NonNull final Value value) {
    super(type);
    _value = value;
  }

  public Value getValue () {
    return _value;
  }
}
