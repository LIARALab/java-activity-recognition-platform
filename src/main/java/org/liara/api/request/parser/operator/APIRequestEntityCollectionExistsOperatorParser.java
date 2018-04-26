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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.liara.api.collection.query.relation.EntityRelation;
import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.filtering.EntityCollectionExistsOperator;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIDocumentedRequestParser;
import org.springframework.lang.NonNull;

import springfox.documentation.service.Parameter;

public class      APIRequestEntityCollectionExistsOperatorParser<Entity, Joined> 
       implements APIRequestEntityCollectionOperatorParser<Entity>, APIDocumentedRequestParser
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final Class<Joined> _joined;
  
  @NonNull
  private final EntityRelation<Entity, Joined> _relation;
  
  @NonNull
  private final APIRequestEntityCollectionOperatorParser<Joined> _joinParser;

  public APIRequestEntityCollectionExistsOperatorParser(
    @NonNull final String field,
    @NonNull final Class<Joined> joined,
    @NonNull final EntityRelation<Entity, Joined> relation,
    @NonNull final APIRequestEntityCollectionOperatorParser<Joined> joinParser 
  ) {
    _field = field;
    _joined = joined;
    _relation = relation;
    _joinParser = joinParser;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      return new EntityCollectionExistsOperator<>(
          _joined, 
          _relation, 
          _joinParser.parse(subRequest)
      );
    } else {
      return new EntityCollectionIdentityOperator<>();
    }
  }

  @Override
  public String getName () {
    return _field;
  }

  @Override
  public List<Parameter> getHandledParametersDocumentation (@NonNull final List<APIDocumentedRequestParser> parents) {
    if (_joinParser instanceof APIDocumentedRequestParser) {
      final List<APIDocumentedRequestParser> nextParents = new ArrayList<>(parents);
      nextParents.add(this);
      return ((APIDocumentedRequestParser) _joinParser).getHandledParametersDocumentation(nextParents);
    }
    
    return Collections.emptyList();
  }
}
