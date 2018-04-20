package org.liara.api.collection.transformation;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class      EntityCollectionAggregationTransformation<Input, Output>
       implements EntityCollectionTransformation<Input, Output>
{
  @NonNull
  private final EntityFieldSelector<Input, Expression<Output>> _selector;

  public EntityCollectionAggregationTransformation(
    @NonNull final EntityFieldSelector<Input, Expression<Output>> selector
  ) {
    _selector = selector;
  }

  @Override
  public CriteriaQuery<Output> transformCriteria (
    @NonNull final EntityCollectionQuery<Input, Output> collectionQuery, 
    @NonNull final CriteriaQuery<Output> query
  ) {
    query.select(_selector.select(collectionQuery));
    return query;
  }
}
