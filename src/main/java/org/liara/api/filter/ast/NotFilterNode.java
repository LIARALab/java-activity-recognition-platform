package org.liara.api.filter.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.aspectj.weaver.Iterators;
import org.springframework.lang.NonNull;

public class NotFilterNode extends BaseFilterNode implements CompositeFilterNode<PredicateFilterNode>, PredicateFilterNode
{
  @NonNull
  private final PredicateFilterNode _predicate;

  public NotFilterNode(@NonNull final PredicateFilterNode predicate) {
    super(CommonFilterNodeType.NOT);
    _predicate = predicate;
  }
  
  public PredicateFilterNode getPredicate () {
    return _predicate;
  }

  @Override
  public Iterator<PredicateFilterNode> iterator () {
    return Iterators.one(_predicate);
  }

  @Override
  public PredicateFilterNode getChild (final int index) throws IndexOutOfBoundsException {
    if (index == 0) return _predicate;
    else throw new IndexOutOfBoundsException();
  }

  @Override
  public Collection<PredicateFilterNode> getChildren () {
    return Arrays.asList(_predicate);
  }

  @Override
  public int getChildCount () {
    return 1;
  }
}
