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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class BetweenFilterNode<Value extends Comparable<? super Value>> extends BaseFilterNode implements CompositeFilterNode<ValueFilterNode<Value>>, PredicateFilterNode
{
  @NonNull
  private final ValueFilterNode<Value> _minimum;

  @NonNull
  private final ValueFilterNode<Value> _maximum;

  public BetweenFilterNode(@NonNull final ValueFilterNode<Value> a, @NonNull final ValueFilterNode<Value> b) {
    super(CommonFilterNodeType.BETWEEN);
    if (a.getValue().compareTo(b.getValue()) < 0) {
      _minimum = a;
      _maximum = b;
    } else {
      _minimum = b;
      _maximum = a;
    }
  }
  
  public Value getMinimum () {
    return _minimum.getValue();
  }
  
  public ValueFilterNode<Value> getMinimumNode () {
    return _minimum;
  }
  
  public Value getMaximum () {
    return _maximum.getValue();
  }
  
  public ValueFilterNode<Value> getMaximumNode () {
    return _maximum;
  }

  @Override
  public Iterator<ValueFilterNode<Value>> iterator () {
    return Arrays.asList(_minimum, _maximum).iterator();
  }

  @Override
  public ValueFilterNode<Value> getChild (final int index) throws IndexOutOfBoundsException {
    switch (index) {
      case 0: return _minimum;
      case 1: return _maximum;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Collection<ValueFilterNode<Value>> getChildren () {
    return Arrays.asList(_minimum, _maximum);
  }

  @Override
  public int getChildCount () {
    return 2;
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((_maximum == null) ? 0 : _maximum.hashCode());
    result = prime * result + ((_minimum == null) ? 0 : _minimum.hashCode());
    return result;
  }

  @Override
  public boolean equals (@Nullable final Object object) {
    if (this == object) return true;
    if (!super.equals(object)) return false;
    if (getClass() != object.getClass()) return false;
    BetweenFilterNode<?> other = (BetweenFilterNode<?>) object;
    if (_maximum == null) {
      if (other._maximum != null) return false;
    } else if (!_maximum.equals(other._maximum)) return false;
    if (_minimum == null) {
      if (other._minimum != null) return false;
    } else if (!_minimum.equals(other._minimum)) return false;
    return true;
  }
}
