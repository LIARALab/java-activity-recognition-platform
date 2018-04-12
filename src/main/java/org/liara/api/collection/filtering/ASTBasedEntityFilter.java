package org.liara.api.collection.filtering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.visitor.criteria.CriteriaFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaFilterASTVisitorContext;
import org.springframework.lang.NonNull;

public class ASTBasedEntityFilter<Entity, Field> implements EntityFilter<Entity>
{  
  @NonNull
  private final PredicateFilterNode _filter;
  
  @NonNull
  private final CriteriaFilterASTVisitor<Entity, Field> _visitor;

  public ASTBasedEntityFilter(
    @NonNull final PredicateFilterNode filter,
    @NonNull final CriteriaFilterASTVisitor<Entity, Field> visitor
  )
  {
    _filter = filter;
    _visitor = visitor;
  }
  
  public CriteriaFilterASTVisitorContext<Entity> getContext (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    return new CriteriaFilterASTVisitorContext<Entity>(
        builder, 
        query, 
        query.getEntity()
    );
  }
  
  public Predicate create (@NonNull final CriteriaBuilder builder, @NonNull final EntityCollectionQuery<Entity, ?> query) {
    return _visitor.visit(
      this.getContext(builder, query),
      _filter
    );
  }
}
