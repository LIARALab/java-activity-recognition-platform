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
