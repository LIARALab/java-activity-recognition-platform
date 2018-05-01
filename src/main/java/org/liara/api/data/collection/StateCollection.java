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
package org.liara.api.data.collection;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.StateCollectionRequestConfiguration;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@DefaultCollectionRequestConfiguration(StateCollectionRequestConfiguration.class)
public class StateCollection extends EntityCollection<State>
{
  @Autowired
  public StateCollection(@NonNull final EntityManager entityManager) {
    super(entityManager, State.class);
  }
  
  public StateCollection (
    @NonNull final StateCollection toCopy  
  ) { super(toCopy); }
  
  public StateCollection (
    @NonNull final StateCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<State> operator
  ) { super(collection, operator); }
  
  @Override
  public StateCollection apply (@NonNull final EntityCollectionOperator<State> operator) {
    return new StateCollection(this, getOperator().conjugate(operator));
  }
  
  public StateCollection of (@NonNull final Sensor sensor) {
    final EntityCollectionOperator<State> operator = query -> query.andWhere(query.getEntity().get("_sensor").in(sensor));
    return apply(operator);
  }
  
  public StateCollection orderedAscBy (@NonNull final SimpleEntityFieldSelector<State, Expression<?>> field) {
    return orderedAscBy((EntityFieldSelector<State, Expression<?>>) field);
  }
  
  public StateCollection orderedAscBy (@NonNull final EntityFieldSelector<State, Expression<?>> field) {
    final EntityCollectionOperator<State> operator = query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andOrderBy(builder.asc(field.select(query)));
    };
    
    return apply(operator);
  }
}
