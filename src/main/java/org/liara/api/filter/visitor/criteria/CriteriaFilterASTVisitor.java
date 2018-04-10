package org.liara.api.filter.visitor.criteria;

import javax.persistence.criteria.Predicate;

import org.liara.api.filter.ast.PredicateFilterNode;
import org.springframework.lang.NonNull;

public interface CriteriaFilterASTVisitor<Entity, Value>
{
  public Predicate visit(
    @NonNull final CriteriaFilterASTVisitorContext<Entity> context, 
    @NonNull final PredicateFilterNode predicate
  );
}
