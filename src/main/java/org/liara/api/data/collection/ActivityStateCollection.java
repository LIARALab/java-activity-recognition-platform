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

import javax.persistence.EntityManagerFactory;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.ActivityStateCollectionRequestConfiguration;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.operators.StateOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@DefaultCollectionRequestConfiguration(ActivityStateCollectionRequestConfiguration.class)
public class ActivityStateCollection extends EntityCollection<ActivityState>
{
  @Autowired
  public ActivityStateCollection (
    @NonNull final EntityManagerFactory entityManagerFactory
  ) { 
    super(entityManagerFactory, ActivityState.class); 
  }
  
  public ActivityStateCollection (
    @NonNull final ActivityStateCollection toCopy  
  ) { 
    super(toCopy);
  }
  
  public ActivityStateCollection (
    @NonNull final ActivityStateCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<ActivityState> operator
  ) { 
    super(collection, operator); 
  }

  @Override
  public ActivityStateCollection apply (@NonNull final EntityCollectionOperator<ActivityState> operator) {
    return new ActivityStateCollection(this, getOperator().conjugate(operator));
  }
  
  public ActivityStateCollection of (@NonNull final Sensor sensor) {
    return apply(StateOperators.of(sensor));
  }
  
  public ActivityStateCollection of (@NonNull final Sensor[] sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivityStateCollection of (@NonNull final Collection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivityStateCollection of (@NonNull final Iterator<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivityStateCollection of (@NonNull final EntityCollection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }
  
  public ActivityStateCollection before (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.before(date));
  }
  
  public ActivityStateCollection after (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.after(date));
  }
  
  public ActivityStateCollection beforeOrAt (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.beforeOrAt(date));
  }
  
  public ActivityStateCollection afterOrAt (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.afterOrAt(date));
  }
}
