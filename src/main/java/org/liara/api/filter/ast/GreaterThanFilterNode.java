package org.liara.api.filter.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.aspectj.weaver.Iterators;
import org.springframework.lang.NonNull;

public class GreaterThanFilterNode<Value extends Comparable<? super Value>> extends BaseFilterNode implements CompositeFilterNode<ValueFilterNode<Value>>, PredicateFilterNode
{
  @NonNull
  private final ValueFilterNode<Value> _minimum;

  public GreaterThanFilterNode(@NonNull final ValueFilterNode<Value> minimum) {
    super(CommonFilterNodeType.GREATHER_THAN);
    _minimum = minimum;
  }
  
  public Value getMinimum () {
    return _minimum.getValue();
  }
  
  public ValueFilterNode<Value> getMinimumNode () {
    return _minimum;
  }

  @Override
  public Iterator<ValueFilterNode<Value>> iterator () {
    return Iterators.one(_minimum);
  }

  @Override
  public ValueFilterNode<Value> getChild (final int index) throws IndexOutOfBoundsException {
    if (index == 0) return _minimum;
    else throw new IndexOutOfBoundsException();
  }

  @Override
  public Collection<ValueFilterNode<Value>> getChildren () {
    return Arrays.asList(_minimum);
  }

  @Override
  public int getChildCount () {
    return 1;
  }
}
