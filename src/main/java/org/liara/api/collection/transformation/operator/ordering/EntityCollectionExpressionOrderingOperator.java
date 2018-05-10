/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.collection.transformation.operator.ordering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public final class      EntityCollectionExpressionOrderingOperator<Entity, Field> 
             implements EntityCollectionOrderingOperator<Entity>
{
  @NonNull
  private final EntityFieldSelector<Entity, Expression<Field>> _selector;
  
  @NonNull
  private final EntityCollectionOrderingOperator.Direction _orderType;
  
  /**
   * Create a new ascending ordering operation for the given expression.
   * 
   * @param selector A selector that select an expression from an entity.
   */
  public EntityCollectionExpressionOrderingOperator (
    @NonNull final EntityFieldSelector<Entity, Expression<Field>> selector
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
    @NonNull final EntityFieldSelector<Entity, Expression<Field>> selector, 
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
  
  public EntityCollectionExpressionOrderingOperator<Entity, Field> setSelector (
    @NonNull final EntityFieldSelector<Entity, Expression<Field>> selector
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(selector, _orderType);
  }

  public EntityCollectionOrderingOperator.Direction getDirection () {
    return _orderType;
  }

  public EntityCollectionExpressionOrderingOperator<Entity, Field> setDirection (
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(_selector, direction);
  }
}
