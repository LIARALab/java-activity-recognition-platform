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

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.springframework.lang.NonNull;

public class JoinBasedEntityCollectionQuery<Entity, Related, Result> implements EntityCollectionQuery<Related, Result>
{
  @NonNull
  private final EntityCollectionQuery<Entity, Result> _query;
  
  @NonNull
  private final Join<Entity, Related>          _root;
  
  public JoinBasedEntityCollectionQuery(
    @NonNull final EntityCollectionQuery<Entity, Result> query, 
    @NonNull final Join<Entity, Related> root
  ) {
    _query = query;
    _root = root;
  }

  @Override
  public <NextRelated> EntityCollectionQuery<NextRelated, Result> joinCollection (@NonNull final String name) {
    return new JoinBasedEntityCollectionQuery<>(this, _root.join(name));
  }

  @Override
  public Path<Related> getEntity () {
    return _root;
  }

  @Override
  public CriteriaQuery<Result> getCriteriaQuery () {
    return _query.getCriteriaQuery();
  }

  @Override
  public CompilableCriteria getCompilableCriteria () {
    return _query.getCompilableCriteria();
  }

  @Override
  public <NextRelated, NextResult> EntityCollectionSubQuery<Related, Result, NextRelated, NextResult> subquery (
    @NonNull final Class<NextRelated> relatedClass,
    @NonNull final Class<NextResult> resultClass
  )
  { 
    final Subquery<NextResult> subquery = _query.subquery(resultClass);
    final Root<NextRelated> root = subquery.from(relatedClass);
        
    final EntityCollectionSubQuery<Related, Result, NextRelated, NextResult> query = new RootBasedEntityCollectionSubQuery<>(
        this, subquery, root
    );
    
    return query;
  }

  @Override
  public Path<Related> correlateEntity (@NonNull final EntityCollectionSubQuery<Related, ?, ?, ?> subQuery) {
    return subQuery.correlate(_root);
  }
}
