package org.liara.api.filter.ast;

import java.util.Collection;

public interface CompositeFilterNode<Child extends FilterNode> extends FilterNode, Iterable<Child>
{
  public Child getChild (final int index) throws IndexOutOfBoundsException;
  public Collection<Child> getChildren ();
  public int getChildCount ();
}

