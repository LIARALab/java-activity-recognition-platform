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
package org.liara.api.request.parser.operator.ordering;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionExpressionOrderingOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.springframework.lang.NonNull;

public class      APIRequestFieldOrderingProcessor<Entity> 
       implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _selector;
  
  public APIRequestFieldOrderingProcessor (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _parameter = parameter;
    _selector = selector;
  }

  @Override
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(_selector, direction);
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key,
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    return key.equals(_parameter);
  }
}
