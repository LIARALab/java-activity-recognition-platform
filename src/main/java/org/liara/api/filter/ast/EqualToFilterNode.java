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
}
