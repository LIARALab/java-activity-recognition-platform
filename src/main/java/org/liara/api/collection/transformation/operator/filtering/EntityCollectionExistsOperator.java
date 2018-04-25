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
package org.liara.api.collection.transformation.operator.filtering;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubquery;
import org.liara.api.collection.query.relation.EntityRelation;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.springframework.lang.NonNull;

public class EntityCollectionExistsOperator<Entity, Joined> implements EntityCollectionOperator<Entity>
{
  @NonNull
  private final Class<Joined> _type;

  @NonNull
  private final EntityRelation<Entity, Joined> _relation;

  @NonNull
  private final EntityCollectionOperator<Joined> _operator;
  
  public EntityCollectionExistsOperator(
    @NonNull final Class<Joined> type,
    @NonNull final EntityRelation<Entity, Joined> relation,
    @NonNull final EntityCollectionOperator<Joined> operator
  )
  {
    _type = type;
    _relation = relation;
    _operator = operator;
  }

  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity, ?> query) {
    final EntityCollectionSubquery<Joined, Joined> subquery = query.subquery(_type, _type);
    _relation.apply(query, subquery);
    _operator.apply(subquery);
    
    query.andWhere(
      query.getManager()
           .getCriteriaBuilder()
           .exists(subquery.getSubquery())
    );
  }
}
