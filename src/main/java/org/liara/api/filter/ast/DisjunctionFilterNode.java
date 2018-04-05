package org.liara.api.filter.ast;

import java.util.Iterator;

import org.springframework.lang.NonNull;

public class DisjunctionFilterNode extends BaseCompositeFilterNode<PredicateFilterNode> implements PredicateFilterNode
{
  public DisjunctionFilterNode(@NonNull final Iterable<PredicateFilterNode> nodes) {
    super(CommonFilterNodeType.DISJUNCTION, nodes);
  }

  public DisjunctionFilterNode(@NonNull final Iterator<PredicateFilterNode> nodes) {
    super(CommonFilterNodeType.DISJUNCTION, nodes);
  }

  public DisjunctionFilterNode(@NonNull final PredicateFilterNode... nodes) {
    super(CommonFilterNodeType.DISJUNCTION, nodes);
  }
}
