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
package org.liara.api.collection.query;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.lang.NonNull;

public class RootBasedEntityCollectionSubQuery<Entity, Result, Related, RelatedResult> implements EntityCollectionSubQuery<Entity, Result, Related, RelatedResult>
{
  @NonNull
  private final EntityCollectionQuery<Entity, Result> _parent;
  
  @NonNull
  private final Subquery<RelatedResult> _subQuery;
  
  @NonNull
  private final Root<Related>          _root;

  @NonNull
  private final Map<String, EntityCollectionQuery<?, RelatedResult>> _joins = new HashMap<>();
  
  public RootBasedEntityCollectionSubQuery(
    @NonNull final EntityCollectionQuery<Entity, Result> parent, 
    @NonNull final Subquery<RelatedResult> subQuery,
    @NonNull final Root<Related> root
  ) {
    _parent = parent;
    _subQuery = subQuery;
    _root = root;
  }

  @Override
  public EntityCollectionQuery<Entity, Result> getParentQuery () {
    return _parent;
  }

  @Override
  public Subquery<RelatedResult> getSubQuery () {
    return _subQuery;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <NextRelated> EntityCollectionQuery<NextRelated, RelatedResult> joinCollection (@NonNull final String name) {
    if (!_joins.containsKey(name)) {
      _joins.put(name, new JoinBasedEntityCollectionSubQuery<>(_parent, _subQuery, _root.join(name)));
    }
    
    return (EntityCollectionQuery<NextRelated, RelatedResult>) _joins.get(name);
  }

  @Override
  public Path<Related> getEntity () {
    return _root;
  }

  @Override
  public <NextRelated, NextResult> EntityCollectionSubQuery<Related, RelatedResult, NextRelated, NextResult> subquery (
    @NonNull final Class<NextRelated> relatedClass,
    @NonNull final Class<NextResult> resultClass
  )
  { 
    final Subquery<NextResult> subquery = _subQuery.subquery(resultClass);
    final Root<NextRelated> root = subquery.from(relatedClass);
        
    final EntityCollectionSubQuery<Related, RelatedResult, NextRelated, NextResult> query = new RootBasedEntityCollectionSubQuery<>(
        this, subquery, root
    );
    
    return query;
  }

  @Override
  public Path<Related> correlateEntity (@NonNull final EntityCollectionSubQuery<Related, ?, ?, ?> subQuery) {
    return subQuery.correlate(_root);
  }
}
