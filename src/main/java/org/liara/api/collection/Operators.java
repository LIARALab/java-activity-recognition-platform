package org.liara.api.collection;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionExpressionOrderingOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.springframework.lang.NonNull;

public final class Operators
{
  public static <Entity> EntityCollectionOperator<Entity> orderAscendingBy (
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.ASC
    );
  }

  public static <Entity> EntityCollectionOperator<Entity> orderAscendingBy (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.ASC
    );
  }
  
  public static <Entity> EntityCollectionOperator<Entity> orderDescendingBy (
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.DESC
    );
  }

  public static <Entity> EntityCollectionOperator<Entity> orderDescendingBy (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.DESC
    );
  }
}
