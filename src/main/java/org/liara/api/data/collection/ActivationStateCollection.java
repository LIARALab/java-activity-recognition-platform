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

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.ActivationStateCollectionRequestConfiguration;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.operators.StateOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@DefaultCollectionRequestConfiguration(ActivationStateCollectionRequestConfiguration.class)
public class ActivationStateCollection extends EntityCollection<ActivationState>
{
  @Autowired
  public ActivationStateCollection (
    @NonNull final EntityManager entityManager
  ) { super(entityManager, ActivationState.class); }
  
  public ActivationStateCollection (
    @NonNull final ActivationStateCollection toCopy  
  ) { super(toCopy); }
  
  public ActivationStateCollection (
    @NonNull final ActivationStateCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<ActivationState> operator
  ) { super(collection, operator); }

  @Override
  public ActivationStateCollection apply (@NonNull final EntityCollectionOperator<ActivationState> operator) {
    return new ActivationStateCollection(this, getOperator().conjugate(operator));
  }
  
  public ActivationStateCollection presences () {
    final EntityCollectionOperator<ActivationState> operator = query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.or(
          builder.like(
            query.getEntity().join("_node").get("_type"),
            "common/room/%"
          ),
          builder.equal(
            query.getEntity().join("_node").get("_type"),
            "common/room"
          )
        )
      );
    };
    
    return apply(operator);
  }
  
  public ActivationStateCollection uses () {
    final EntityCollectionOperator<ActivationState> operator = query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.or(
          builder.like(
            query.getEntity().join("_node").get("_type"),
            "common/furniture/%"
          ),
          builder.equal(
            query.getEntity().join("_node").get("_type"),
            "common/furniture"
          )
        )
      );
    };
    
    return apply(operator);
  }
  
  public ActivationStateCollection of (@NonNull final Sensor sensor) {
    return apply(StateOperators.of(sensor));
  }
  
  public ActivationStateCollection of (@NonNull final Sensor[] sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivationStateCollection of (@NonNull final Collection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivationStateCollection of (@NonNull final Iterator<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivationStateCollection of (@NonNull final EntityCollection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
}
