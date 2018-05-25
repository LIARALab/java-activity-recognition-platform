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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.aspectj.weaver.Iterators;
import org.springframework.lang.NonNull;

public class EqualToFilterNode<Value extends Comparable<? super Value>> extends BaseFilterNode implements CompositeFilterNode<ValueFilterNode<Value>>, PredicateFilterNode
{
  @NonNull
  private final ValueFilterNode<Value> _value;

  public EqualToFilterNode(@NonNull final ValueFilterNode<Value> value) {
    super(CommonFilterNodeType.EQUAL_TO);
    _value = value;
  }
  
  public Value getValue () {
    return _value.getValue();
  }
  
  public ValueFilterNode<Value> getValueNode () {
    return _value;
  }

  @Override
  public Iterator<ValueFilterNode<Value>> iterator () {
    return Iterators.one(_value);
  }

  @Override
  public ValueFilterNode<Value> getChild (final int index) throws IndexOutOfBoundsException {
    if (index == 0) return _value;
    else throw new IndexOutOfBoundsException();
  }

  @Override
  public Collection<ValueFilterNode<Value>> getChildren () {
    return Arrays.asList(_value);
  }

  @Override
  public int getChildCount () {
    return 1;
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((_value == null) ? 0 : _value.hashCode());
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    EqualToFilterNode<?> other = (EqualToFilterNode<?>) obj;
    if (_value == null) {
      if (other._value != null) return false;
    } else if (!_value.equals(other._value)) return false;
    return true;
  }
}
