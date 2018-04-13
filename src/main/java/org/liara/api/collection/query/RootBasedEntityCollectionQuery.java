/*******************************************************************************
 * Copyright (C) 2018 C�dric DEMONGIVERT <cedric.demongivert@gmail.com>
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

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.springframework.lang.NonNull;

public class RootBasedEntityCollectionQuery<Entity, Result> implements EntityCollectionQuery<Entity, Result>
{
  @NonNull
  private final CriteriaQuery<Result> _query;
  
  @NonNull
  private final CompilableCriteria    _compilable;
  
  @NonNull
  private final Root<Entity>          _root;
  
  @NonNull
  private final Map<String, EntityCollectionQuery<?, Result>> _joins = new HashMap<>();

  public RootBasedEntityCollectionQuery(
    @NonNull final CriteriaQuery<Result> query, 
    @NonNull final Class<Entity> entity
  ) {
    _query = query;
    _compilable = (CompilableCriteria) query;
    _root = query.from(entity);
  }
  
  public RootBasedEntityCollectionQuery(
    @NonNull final CriteriaQuery<Result> query, 
    @NonNull final Root<Entity> root
  ) {
    _query = query;
    _compilable = (CompilableCriteria) query;
    _root = root;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Related> EntityCollectionQuery<Related, Result> joinCollection (@NonNull final String name) {
    if (!_joins.containsKey(name)) {
      _joins.put(name, new JoinBasedEntityCollectionQuery<>(this, _root.join(name)));
    }
    
    return (EntityCollectionQuery<Related, Result>) _joins.get(name);
  }

  @Override
  public Path<Entity> getEntity () {
    return _root;
  }

  @Override
  public CriteriaQuery<Result> getCriteriaQuery () {
    return _query;
  }

  @Override
  public CompilableCriteria getCompilableCriteria () {
    return _compilable;
  }

  @Override
  public <NextRelated, NextResult> EntityCollectionSubQuery<Entity, Result, NextRelated, NextResult> subquery (
    @NonNull final Class<NextRelated> relatedClass,
    @NonNull final Class<NextResult> resultClass
  )
  { 
    final Subquery<NextResult> subquery = _query.subquery(resultClass);
    final Root<NextRelated> root = subquery.from(relatedClass);
        
    final EntityCollectionSubQuery<Entity, Result, NextRelated, NextResult> query = new RootBasedEntityCollectionSubQuery<>(
        this, subquery, root
    );
    
    return query;
  }

  @Override
  public Path<Entity> correlateEntity (@NonNull final EntityCollectionSubQuery<Entity, ?, ?, ?> subQuery) {
    return subQuery.correlate(_root);
  }
}
