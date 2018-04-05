package org.liara.api.filter.ast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.lang.NonNull;

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
    @NonNull final Child... nodes
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
}
