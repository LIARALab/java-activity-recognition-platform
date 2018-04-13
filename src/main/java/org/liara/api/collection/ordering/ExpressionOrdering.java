package org.liara.api.collection.ordering;

import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public final class ExpressionOrdering<Entity> implements Ordering<Entity>
{
  @NonNull
  private final SimplifiedCriteriaExpressionSelector<?> _selector;
  
  @NonNull
  private final OrderingDirection _orderType;

  public ExpressionOrdering (
    @NonNull final SimplifiedCriteriaExpressionSelector<?> selector, 
    @NonNull final OrderingDirection orderType
  ) {
    _selector = selector;
    _orderType = orderType;
  }

  @Override
  public List<Order> create (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    switch (_orderType) {
      case DESC:
        return Collections.singletonList(builder.desc(getField(builder, query)));
      case ASC:
      default:
        return Collections.singletonList(builder.asc(getField(builder, query)));
    }
  }
  
  public Expression<?> getField (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    return _selector.select(builder, query, query.getEntity());
  }

  public SimplifiedCriteriaExpressionSelector<?> getSelector () {
    return _selector;
  }

  public OrderingDirection getOrderType () {
    return _orderType;
  }
}
