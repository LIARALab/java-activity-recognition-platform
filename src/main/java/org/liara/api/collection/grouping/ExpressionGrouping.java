package org.liara.api.collection.grouping;

import java.util.Collections;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public class ExpressionGrouping<Entity> implements EntityGrouping<Entity>
{
  @NonNull
  private final SimplifiedCriteriaExpressionSelector<?> _selector;
  
  public ExpressionGrouping(@NonNull final SimplifiedCriteriaExpressionSelector<?> selector) {
    _selector = selector;
  }

  @Override
  public List<Selection<?>> createSelection (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    return Collections.singletonList(_selector.select(builder, query, query.getEntity()));
  }

  @Override
  public List<Expression<?>> createGroupBy (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    return Collections.singletonList(_selector.select(builder, query, query.getEntity()));
  }
}
