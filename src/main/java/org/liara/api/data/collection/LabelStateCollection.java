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

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.LabelStateCollectionRequestConfiguration;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.operators.StateOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;

@Component
@DefaultCollectionRequestConfiguration(LabelStateCollectionRequestConfiguration.class)
public class LabelStateCollection
  extends EntityCollection<LabelState>
{
  @Autowired
  public LabelStateCollection (
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, LabelState.class);
  }

  public LabelStateCollection (
    @NonNull final LabelStateCollection toCopy
  ) { 
    super(toCopy);
  }

  public LabelStateCollection (
    @NonNull final LabelStateCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<LabelState> operator
  ) { 
    super(collection, operator); 
  }

  @Override
  public LabelStateCollection apply (@NonNull final EntityCollectionOperator<LabelState> operator) {
    return new LabelStateCollection(this, getOperator().conjugate(operator));
  }

  public LabelStateCollection of (@NonNull final Sensor sensor) {
    return apply(StateOperators.of(sensor));
  }

  public LabelStateCollection of (@NonNull final Sensor[] sensors) {
    return apply(StateOperators.of(sensors));
  }

  public LabelStateCollection of (@NonNull final Collection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }

  public LabelStateCollection of (@NonNull final Iterator<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }

  public LabelStateCollection of (@NonNull final EntityCollection<Sensor> sensors) {
    return apply(StateOperators.of(sensors));
  }

  public LabelStateCollection before (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.before(date));
  }

  public LabelStateCollection after (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.after(date));
  }

  public LabelStateCollection beforeOrAt (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.beforeOrAt(date));
  }

  public LabelStateCollection afterOrAt (@NonNull final ZonedDateTime date) {
    return apply(StateOperators.afterOrAt(date));
  }
}
