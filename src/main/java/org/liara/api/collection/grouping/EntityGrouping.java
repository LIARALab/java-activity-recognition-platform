package org.liara.api.collection.grouping;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public interface EntityGrouping<Entity>
{
  public default void apply (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    query.multiselect(createSelection(builder, query));
    query.groupBy(createGroupBy(builder, query));
  }
  
  public List<Selection<?>> createSelection (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  );
  
  public List<Expression<?>> createGroupBy (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  );
}
