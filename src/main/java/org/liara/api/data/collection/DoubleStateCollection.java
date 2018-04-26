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

import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.DoubleStateCollectionRequestConfiguration;
import org.liara.api.collection.EntityCollection;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.DoubleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@DefaultCollectionRequestConfiguration(DoubleStateCollectionRequestConfiguration.class)
public class DoubleStateCollection extends EntityCollection<DoubleState>
{
  @Autowired
  public DoubleStateCollection(@NonNull final EntityManager entityManager) {
    super(entityManager, DoubleState.class);
  }

  public DoubleStateCollection (
    @NonNull final DoubleStateCollection toCopy  
  ) { super(toCopy); }
  
  public DoubleStateCollection (
    @NonNull final DoubleStateCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<DoubleState> operator
  ) { super(collection, operator); }
  
  @Override
  public DoubleStateCollection apply (@NonNull final EntityCollectionOperator<DoubleState> operator) {
    return new DoubleStateCollection(this, getOperator().conjugate(operator));
  }
}
