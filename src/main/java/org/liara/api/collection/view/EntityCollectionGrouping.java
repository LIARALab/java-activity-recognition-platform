package org.liara.api.collection.view;

import java.util.List;

import javax.persistence.Tuple;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;

public class EntityCollectionGrouping<Entity>
       implements View<List<Tuple>>
{
  @NonNull
  private final EntityCollectionAggregation<Entity, ?> _aggregation;
  
  @NonNull
  private final EntityCollectionGroupTransformation<Entity> _group;
  
  public EntityCollectionGrouping(
    @NonNull final EntityCollectionAggregation<Entity, ?> aggregation,
    @NonNull final EntityCollectionGroupTransformation<Entity> group
  ) {
    _aggregation = aggregation;
    _group = group;
  }
  
  public EntityCollectionMainQuery<Entity, Tuple> createQuery () {
    final EntityCollectionMainQuery<Entity, Tuple> query = _aggregation.createCollectionQuery(Tuple.class);
    _group.apply(query);
    
    return query;
  }

  @Override
  public List<Tuple> get () {
    final EntityCollectionMainQuery<Entity, Tuple> query = createQuery();
    return query.getManager().createQuery(query.getCriteriaQuery()).getResultList();
  }
  
  public EntityCollectionAggregation<Entity, ?> getAggregation () {
    return _aggregation;
  }
  
  public EntityCollectionGroupTransformation<Entity> getGroupTransformation () {
    return _group;
  }
}
