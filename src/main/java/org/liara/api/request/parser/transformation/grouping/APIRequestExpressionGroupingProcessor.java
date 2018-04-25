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
package org.liara.api.request.parser.transformation.grouping;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.grouping.EntityCollectionFieldGroupingTransformation;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;

public class      APIRequestExpressionGroupingProcessor<Entity> 
       implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final String _key;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _selector;
  
  public APIRequestExpressionGroupingProcessor (
    @NonNull final String key, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _key = key;
    _selector = selector;
  }
  
  public APIRequestExpressionGroupingProcessor (
    @NonNull final String key, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _key = key;
    _selector = selector;
  }

  @Override
  public EntityCollectionGroupTransformation<Entity> process (
    @NonNull final String key
  ) {
    return new EntityCollectionFieldGroupingTransformation<>(_selector);
  }

  @Override
  public boolean hableToProcess (@NonNull final String key) {
    return key.equals(_key);
  }
}
