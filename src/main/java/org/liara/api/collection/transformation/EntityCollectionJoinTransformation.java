package org.liara.api.collection.transformation;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class EntityCollectionJoinTransformation<Entity, Joined, Output>
       implements Transformation<Entity, Output>
{
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @NonNull
  private final Transformation<Joined, Output> _transformation;
  
  public EntityCollectionJoinTransformation(
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join,
    @NonNull final Transformation<Joined, Output> transformation
  ) {
    _join = join;
    _transformation = transformation;
  }

  @Override
  public CriteriaQuery<Output> transformCriteria (
    @NonNull final EntityCollectionQuery<Entity, Output> collectionQuery, 
    @NonNull final CriteriaQuery<Output> query
  ) {
    return _transformation.transformCriteria(
      collectionQuery.join(_join), 
      query
    );
  }

  @Override
  public TypedQuery<Output> transformQuery (
    @NonNull final TypedQuery<Output> query
  ) {
    return _transformation.transformQuery(query);
  }
}
