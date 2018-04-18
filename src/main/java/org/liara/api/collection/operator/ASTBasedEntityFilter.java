package org.liara.api.collection.operator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.visitor.collection.CollectionQueryFilterVisitorContext;
import org.liara.api.filter.visitor.collection.EntityCollectionFilterVisitor;
import org.springframework.lang.NonNull;

public class ASTBasedEntityFilter<Entity, Field> implements EntityCollectionFilteringOperator<Entity>
{  
  @NonNull
  private final PredicateFilterNode _filter;
  
  @NonNull
  private final EntityCollectionFilterVisitor<Entity, Field> _visitor;

  public ASTBasedEntityFilter(
    @NonNull final PredicateFilterNode filter,
    @NonNull final EntityCollectionFilterVisitor<Entity, Field> visitor
  )
  {
    _filter = filter;
    _visitor = visitor;
  }
  
  public CollectionQueryFilterVisitorContext<Entity> getContext (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    return new CollectionQueryFilterVisitorContext<Entity>(
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

  @Override
  public void apply (EntityCollectionQuery<Entity> query) {
    // TODO Auto-generated method stub
    
  }
}
