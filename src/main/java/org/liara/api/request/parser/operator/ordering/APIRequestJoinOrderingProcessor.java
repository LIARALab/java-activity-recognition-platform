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

import javax.persistence.criteria.Join;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionJoinOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestJoinOrderingProcessor<Entity, Joined> 
       implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @Nullable
  private APIRequestOrderingProcessor<Joined> _processor = null;
    
  public APIRequestJoinOrderingProcessor(
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final APIRequestOrderingProcessor<Joined> processor
  ) {
    _parameter = parameter;
    _join = join;
    _processor = processor;
  }

  @Override
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    final String childKey = key.substring(_parameter.length() + 1);
    
    return new EntityCollectionJoinOperator<>(
        _join, _processor.process(childKey, direction)
    );
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    if (key.startsWith(_parameter + ".")) {
      final String childKey = key.substring(_parameter.length() + 1);
      
      return _processor.hableToProcess(childKey, direction);
    } else {
      return false;
    }
  }
}
