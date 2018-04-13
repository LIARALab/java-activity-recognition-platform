/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.EntityCollectionView;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseRestController
{
  public <Entity, Identifier> ResponseEntity<List<Entity>> indexCollection (
    @NonNull final EntityCollection<Entity, Identifier> collection, 
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    final APIRequest apiRequest = APIRequest.from(request);
    final EntityCollectionView<Entity, Identifier> view = collection.apply(apiRequest);

    if (view.isComplete()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }
  
  public <Entity, Identifier, Value> ResponseEntity<Object> aggregate (
    @NonNull final EntityCollection<Entity, Identifier> collection,
    @NonNull final HttpServletRequest request,
    @NonNull final CriteriaExpressionSelector<Value> aggregation
  )
    throws InvalidAPIRequestException
  {
    return aggregate(collection, request, aggregation, x -> x);
  }

  @SuppressWarnings("unchecked")
  public <Entity, Identifier, Value, Cast> ResponseEntity<Object> aggregate (
    @NonNull final EntityCollection<Entity, Identifier> collection,
    @NonNull final HttpServletRequest request,
    @NonNull final CriteriaExpressionSelector<Value> aggregation,
    @NonNull final Function<Value, Cast> cast
  )
    throws InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    final EntityCollection<Entity, Identifier> filtered = collection.filter(apiRequest).order(apiRequest);
  
    final EntityCollectionQuery<Entity, Tuple> query = filtered.createCollectionQuery(Tuple.class);
    collection.group(query, apiRequest, aggregation);
    final List<Tuple> tuples = filtered.fetch(query);
    final List<Object[]> result = new ArrayList<>();
    
    if (tuples.size() == 1 && tuples.get(0).getElements().size() == 1) {
      return new ResponseEntity<Object>(
        cast.apply((Value) tuples.get(0).get(0)), 
        HttpStatus.OK
      );
    }
    
    for (final Tuple tuple : tuples) {
      final List<TupleElement<?>> elements = tuple.getElements();
      final Object key;
      
      if (elements.size() == 2) {
        key = tuple.get(0);
      } else {
        final List<Object> keyList = new ArrayList<>();
        for (int index = 0; index < elements.size() - 1; ++index) {
          keyList.add(tuple.get(index));
        }
        key = keyList;
      }
      
      result.add(new Object[] {
        key, 
        cast.apply((Value) tuple.get(elements.size() - 1))
      });
    }
    
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }
  
  protected Expression<Long> count (
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Path<?> root
  ) { return builder.count(root); }
}
