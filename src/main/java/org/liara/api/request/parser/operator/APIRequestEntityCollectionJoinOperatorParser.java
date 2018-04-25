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
package org.liara.api.request.parser.operator;

import javax.persistence.criteria.Join;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionJoinOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestEntityCollectionJoinOperatorParser<Entity, Joined> 
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @NonNull
  private final APIRequestEntityCollectionOperatorParser<Joined> _joinParser;

  public APIRequestEntityCollectionJoinOperatorParser (
    @NonNull final String field,
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join,
    @NonNull final APIRequestEntityCollectionOperatorParser<Joined> joinParser 
  ) {
    _field = field;
    _join = join;
    _joinParser = joinParser;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      return new EntityCollectionJoinOperator<>(
          _join,
          _joinParser.parse(subRequest)
      );
    } else {
      return new EntityCollectionIdentityOperator<>();
    }
  }
}
