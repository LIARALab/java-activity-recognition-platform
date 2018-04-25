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

public class RegexpFilterNode extends BaseFilterNode implements CompositeFilterNode<ValueFilterNode<String>>, PredicateFilterNode
{
  @NonNull
  private final ValueFilterNode<String> _value;

  public RegexpFilterNode(@NonNull final ValueFilterNode<String> value) {
    super(CommonFilterNodeType.REGEXP);
    _value = value;
  }
  
  public String getValue () {
    return _value.getValue();
  }
  
  public ValueFilterNode<String> getValueNode () {
    return _value;
  }

  @Override
  public Iterator<ValueFilterNode<String>> iterator () {
    return Iterators.one(_value);
  }

  @Override
  public ValueFilterNode<String> getChild (final int index) throws IndexOutOfBoundsException {
    if (index == 0) return _value;
    else throw new IndexOutOfBoundsException();
  }

  @Override
  public Collection<ValueFilterNode<String>> getChildren () {
    return Arrays.asList(_value);
  }

  @Override
  public int getChildCount () {
    return 1;
  }
}
