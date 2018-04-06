package org.liara.api.filter.visitor.criteria;

import org.liara.api.filter.ast.PredicateFilterNode;
import org.springframework.lang.NonNull;

public interface CriteriaFilterASTVisitor<Entity, Value>
{
  public void visit(
    @NonNull final CriteriaFilterASTVisitorContext<Entity> context, 
    @NonNull final PredicateFilterNode predicate
  );
}
