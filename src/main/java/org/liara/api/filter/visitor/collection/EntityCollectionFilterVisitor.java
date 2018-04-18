package org.liara.api.filter.visitor.collection;

import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.springframework.lang.NonNull;

public interface EntityCollectionFilterVisitor<Entity, Value>
{
  public Predicate filter (
    @NonNull final EntityCollectionQuery<Entity> query, 
    @NonNull final PredicateFilterNode predicate
  );
}
