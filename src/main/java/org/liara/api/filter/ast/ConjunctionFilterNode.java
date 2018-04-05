package org.liara.api.filter.ast;

import java.util.Iterator;

import org.springframework.lang.NonNull;

public class ConjunctionFilterNode extends BaseCompositeFilterNode<PredicateFilterNode> implements PredicateFilterNode
{
  public ConjunctionFilterNode(@NonNull final Iterable<PredicateFilterNode> nodes) {
    super(CommonFilterNodeType.CONJUNCTION, nodes);
  }

  public ConjunctionFilterNode(@NonNull final Iterator<PredicateFilterNode> nodes) {
    super(CommonFilterNodeType.CONJUNCTION, nodes);
  }

  public ConjunctionFilterNode(@NonNull final PredicateFilterNode... nodes) {
    super(CommonFilterNodeType.CONJUNCTION, nodes);
  }
}
