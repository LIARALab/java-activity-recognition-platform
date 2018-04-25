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
package org.liara.api.collection.view;

import java.util.List;

import javax.persistence.Tuple;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;

public class EntityCollectionGrouping<Entity>
       implements View<List<Tuple>>
{
  @NonNull
  private final EntityCollectionAggregation<Entity, ?> _aggregation;
  
  @NonNull
  private final EntityCollectionGroupTransformation<Entity> _group;
  
  public EntityCollectionGrouping(
    @NonNull final EntityCollectionAggregation<Entity, ?> aggregation,
    @NonNull final EntityCollectionGroupTransformation<Entity> group
  ) {
    _aggregation = aggregation;
    _group = group;
  }
  
  public EntityCollectionMainQuery<Entity, Tuple> createQuery () {
    final EntityCollectionMainQuery<Entity, Tuple> query = _aggregation.createCollectionQuery(Tuple.class);
    _group.apply(query);
    
    return query;
  }

  @Override
  public List<Tuple> get () {
    final EntityCollectionMainQuery<Entity, Tuple> query = createQuery();
    return query.getManager().createQuery(query.getCriteriaQuery()).getResultList();
  }
  
  public EntityCollectionAggregation<Entity, ?> getAggregation () {
    return _aggregation;
  }
  
  public EntityCollectionGroupTransformation<Entity> getGroupTransformation () {
    return _group;
  }
}
