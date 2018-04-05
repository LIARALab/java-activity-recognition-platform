package org.liara.api.filter.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.lang.NonNull;

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
}
