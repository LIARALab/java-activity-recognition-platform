package org.liara.api.collection.transformation.aggregation;

import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

public class      ExpressionAggregationTransformation<Entity, AggregationResult> 
       implements EntityAggregationTransformation<Entity, AggregationResult>
{
  @NonNull
  private final EntityFieldSelector<Entity, Selection<AggregationResult>> _aggregation;
  
  @NonNull
  private final Class<AggregationResult> _aggregationType;

  public ExpressionAggregationTransformation(
    @NonNull final SimpleEntityFieldSelector<Entity, Selection<AggregationResult>> aggregation,
    @NonNull final Class<AggregationResult> aggregationType
  )
  {
    _aggregation = aggregation;
    _aggregationType = aggregationType;
  }
  
  public ExpressionAggregationTransformation(
    @NonNull final EntityFieldSelector<Entity, Selection<AggregationResult>> aggregation,
    @NonNull final Class<AggregationResult> aggregationType
  )
  {
    _aggregation = aggregation;
    _aggregationType = aggregationType;
  }

  @Override
  public Class<AggregationResult> getAggregationType () {
    return _aggregationType;
  }

  @Override
  public Selection<AggregationResult> aggregate (
    @NonNull final EntityCollectionMainQuery<Entity, ?> query
  ) {
    return _aggregation.select(query);
  }
}
