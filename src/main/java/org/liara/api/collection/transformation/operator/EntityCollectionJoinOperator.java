package org.liara.api.collection.transformation.operator;

import javax.persistence.criteria.Join;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class EntityCollectionJoinOperator<Entity, Joined> implements EntityCollectionOperator<Entity>
{
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _selector;
  
  @NonNull
  private final EntityCollectionOperator<Joined> _operator;
  
  public EntityCollectionJoinOperator(
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> selector,
    @NonNull final EntityCollectionOperator<Joined> operator
  )
  {
    _selector = selector;
    _operator = operator;
  }

  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity, ?> query) {
    final EntityCollectionQuery<Joined, ?> joinQuery = query.join(_selector.select(query));
    _operator.apply(joinQuery);
  }
}
