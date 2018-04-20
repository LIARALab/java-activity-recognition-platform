package org.liara.api.collection.operator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public final class EntityCollectionExpressionOrderingOperator<Entity> implements EntityCollectionOrderingOperator<Entity>
{
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _selector;
  
  @NonNull
  private final EntityCollectionOrderingOperator.Direction _orderType;
  
  /**
   * Create a new ascending ordering operation for the given expression.
   * 
   * @param selector A selector that select an expression from an entity.
   */
  public EntityCollectionExpressionOrderingOperator (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _selector = selector;
    _orderType = EntityCollectionOrderingOperator.Direction.ASC;
  }

  /**
   * Create a new ordering operation for the given expression.
   * 
   * @param selector A selector that select an expression from an entity.
   * @param orderType The order of the ordering for the given selector.
   */
  public EntityCollectionExpressionOrderingOperator (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector, 
    @NonNull final EntityCollectionOrderingOperator.Direction orderType
  ) {
    _selector = selector;
    _orderType = orderType;
  }

  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity, ?> query) {
    query.andOrderBy(createOrderingCriteria(query));
  }

  private Order createOrderingCriteria (
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
    
    switch (_orderType) {
      case DESC:
        return builder.desc(_selector.select(query));
      case ASC:
      default:
        return builder.asc(_selector.select(query));
    }
  }

  public EntityFieldSelector<Entity, ?> getSelector () {
    return _selector;
  }
  
  public EntityCollectionExpressionOrderingOperator<Entity> setSelector (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(selector, _orderType);
  }

  public EntityCollectionOrderingOperator.Direction getDirection () {
    return _orderType;
  }

  public EntityCollectionExpressionOrderingOperator<Entity> setDirection (
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(_selector, direction);
  }
}
