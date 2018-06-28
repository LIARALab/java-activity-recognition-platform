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

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.ActivityStateCollectionRequestConfiguration;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.activity.BaseSleepingActivityState;
import org.liara.api.data.operators.StateOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@DefaultCollectionRequestConfiguration(ActivityStateCollectionRequestConfiguration.class)
public class BaseSleepingActivityStateCollection extends EntityCollection<BaseSleepingActivityState>
{
  @Autowired
  public BaseSleepingActivityStateCollection (
    @NonNull final EntityManager entityManager
  ) { 
    super(entityManager, BaseSleepingActivityState.class); 
  }
  
  public BaseSleepingActivityStateCollection (
    @NonNull final BaseSleepingActivityStateCollection toCopy  
  ) { 
    super(toCopy); 
  }
  
  public BaseSleepingActivityStateCollection (
    @NonNull final BaseSleepingActivityStateCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<BaseSleepingActivityState> operator
  ) { 
    super(collection, operator); 
  }

  @Override
  public BaseSleepingActivityStateCollection apply (
    @NonNull final EntityCollectionOperator<BaseSleepingActivityState> operator
  ) {
    return new BaseSleepingActivityStateCollection(this, getOperator().conjugate(operator));
  }
  
  public BaseSleepingActivityStateCollection of (@NonNull final Sensor sensor) {
    return apply(StateOperators.of(sensor));
  }
  
  public BaseSleepingActivityStateCollection of (@NonNull final Sensor[] sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public BaseSleepingActivityStateCollection of (@NonNull final Collection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public BaseSleepingActivityStateCollection of (@NonNull final Iterator<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public BaseSleepingActivityStateCollection of (@NonNull final EntityCollection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public BaseSleepingActivityStateCollection before (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.before(date));
  }
  
  public BaseSleepingActivityStateCollection after (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.after(date));
  }
  
  public BaseSleepingActivityStateCollection beforeOrAt (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.beforeOrAt(date));
  }
  
  public BaseSleepingActivityStateCollection afterOrAt (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.afterOrAt(date));
  }
}
