package org.liara.api.filter.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.aspectj.weaver.Iterators;
import org.springframework.lang.NonNull;

public class LessThanOrEqualToFilterNode<Value extends Comparable<? super Value>> extends BaseFilterNode implements CompositeFilterNode<ValueFilterNode<Value>>, PredicateFilterNode
{
  @NonNull
  private final ValueFilterNode<Value> _maximum;

  public LessThanOrEqualToFilterNode(@NonNull final ValueFilterNode<Value> maximum) {
    super(CommonFilterNodeType.LESS_THAN_OR_EQUAL_TO);
    _maximum = maximum;
  }
  
  public Value getMaximum () {
    return _maximum.getValue();
  }
  
  public ValueFilterNode<Value> getMaximumNode () {
    return _maximum;
  }

  @Override
  public Iterator<ValueFilterNode<Value>> iterator () {
    return Iterators.one(_maximum);
  }

  @Override
  public ValueFilterNode<Value> getChild (final int index) throws IndexOutOfBoundsException {
    if (index == 0) return _maximum;
    else throw new IndexOutOfBoundsException();
  }

  @Override
  public Collection<ValueFilterNode<Value>> getChildren () {
    return Arrays.asList(_maximum);
  }

  @Override
  public int getChildCount () {
    return 1;
  }
}
