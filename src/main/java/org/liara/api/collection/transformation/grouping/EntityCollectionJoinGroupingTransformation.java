package org.liara.api.collection.transformation.grouping;

import javax.persistence.Tuple;
import javax.persistence.criteria.Join;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class      EntityCollectionJoinGroupingTransformation<Entity, Joined>
       implements EntityCollectionGroupTransformation<Entity>
{
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @NonNull
  private final EntityCollectionGroupTransformation<Joined> _transform;

  public EntityCollectionJoinGroupingTransformation (
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join,
    @NonNull final EntityCollectionGroupTransformation<Joined> transform
  ) {
    _join = join;
    _transform = transform;
  }
  
  @Override
  public void apply (@NonNull final EntityCollectionMainQuery<Entity, Tuple> query) {
    _transform.apply(query.join(_join.select(query)));
  } 
}
