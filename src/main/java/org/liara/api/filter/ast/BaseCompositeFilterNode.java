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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.google.common.collect.ImmutableList;

public class BaseCompositeFilterNode<Child extends FilterNode> extends BaseFilterNode implements CompositeFilterNode<Child>
{
  @NonNull
  private final List<Child> _nodes;
  
  public BaseCompositeFilterNode(
    @NonNull final FilterNodeType type, 
    @NonNull final Iterable<Child> nodes
  ) {
    super(type);
    _nodes = ImmutableList.copyOf(nodes);
  }
  
  public BaseCompositeFilterNode(
    @NonNull final FilterNodeType type, 
    @NonNull final Iterator<Child> nodes
  ) {
    super(type);
    _nodes = ImmutableList.copyOf(nodes);
  }
  
  public BaseCompositeFilterNode(
    @NonNull final FilterNodeType type, 
    @NonNull final Child[] nodes
  ) {
    super(type);
    _nodes = ImmutableList.copyOf(nodes);
  }

  @Override
  public Iterator<Child> iterator () {
    return _nodes.iterator();
  }

  @Override
  public Child getChild (final int index) throws IndexOutOfBoundsException {
    return _nodes.get(index);
  }

  @Override
  public Collection<Child> getChildren () {
    return _nodes;
  }

  @Override
  public int getChildCount () {
    return _nodes.size();
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((_nodes == null) ? 0 : _nodes.hashCode());
    return result;
  }

  @Override
  public boolean equals (@Nullable final Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    BaseCompositeFilterNode<?> other = (BaseCompositeFilterNode<?>) obj;
    if (_nodes == null) {
      if (other._nodes != null) return false;
    } else if (!_nodes.equals(other._nodes)) return false;
    return true;
  }
}
