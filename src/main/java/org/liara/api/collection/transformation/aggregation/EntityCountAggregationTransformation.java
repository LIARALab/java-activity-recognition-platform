package org.liara.api.collection.transformation.aggregation;

import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.springframework.lang.NonNull;

public class EntityCountAggregationTransformation<Entity>
       implements EntityAggregationTransformation<Entity, Long>
{
  public static <Entity> EntityCountAggregationTransformation<Entity> create () {
    return new EntityCountAggregationTransformation<>();
  }
  
  @Override
  public Class<Long> getAggregationType () {
    return Long.class;
  }

  @Override
  public Selection<Long> aggregate (@NonNull final EntityCollectionMainQuery<Entity, ?> query) {
    return query.getManager().getCriteriaBuilder().count(query.getEntity());
  }
}
