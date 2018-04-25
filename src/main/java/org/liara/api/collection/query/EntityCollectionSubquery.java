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
package org.liara.api.collection.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

public class      EntityCollectionSubquery<Entity, Result> 
       extends    AbstractEntityCollectionQuery<Entity, Result>
       implements Expression<Result>
{
  @NonNull
  private final Subquery<Result> _query;
  
  public EntityCollectionSubquery(
    @NonNull final EntityManager manager, 
    @NonNull final Subquery<Result> query, 
    @NonNull final QueriedEntity<?, Entity> entity
  ) {
    super (manager, query, entity);
    _query = query;
  }

  @Override
  public EntityCollectionQuery<Entity, Result> orderBy (@NonNull final Order... o) {
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity, Result> orderBy (@NonNull final List<Order> o) {
    return this;
  }

  @Override
  public List<Order> getOrderList () {
    return new ArrayList<>();
  }
  
  @Override
  public Set<ParameterExpression<?>> getParameters () {
    return Collections.emptySet();
  }

  @Override
  public <Joined> EntityCollectionSubquery<Joined, Result> join (@NonNull final Join<Entity, Joined> join) {
    return new EntityCollectionSubquery<>(getManager(), _query, QueriedEntity.from(join));
  }
  
  @Override
  public <Joined> EntityCollectionSubquery<Joined, Result> join (
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join
  ) {
    return (EntityCollectionSubquery<Joined, Result>) super.join(join);
  }

  @Override
  public <Joined> EntityCollectionSubquery<Joined, Result> join (
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join
  ) {
    return join((EntityFieldSelector<Entity, Join<Entity, Joined>>) join);
  }

  @Override
  public Predicate isNull () {
    return _query.isNull();
  }

  @Override
  public Predicate isNotNull () {
    return _query.isNotNull();
  }

  @Override
  public Predicate in (@NonNull final Object... values) {
    return _query.in(values);
  }

  @Override
  public Predicate in (@NonNull final Expression<?>... values) {
    return _query.in(values);
  }

  @Override
  public Predicate in (@NonNull final Collection<?> values) {
    return _query.in(values);
  }

  @Override
  public Predicate in (@NonNull final Expression<Collection<?>> values) {
    return _query.in(values);
  }

  @Override
  public <X> Expression<X> as (@NonNull final Class<X> type) {
    return _query.as(type);
  }

  @Override
  public Selection<Result> alias (@NonNull final String name) {
    return _query.alias(name);
  }

  @Override
  public boolean isCompoundSelection () {
    return _query.isCompoundSelection();
  }

  @Override
  public List<Selection<?>> getCompoundSelectionItems () {
    return _query.getCompoundSelectionItems();
  }

  @Override
  public Class<? extends Result> getJavaType () {
    return _query.getJavaType();
  }

  @Override
  public String getAlias () {
    return _query.getAlias();
  }

  public <Related> QueriedEntity<?, Related> correlate (@NonNull final QueriedEntity<?, Related> related) {
    return related.correlate(_query);
  }
  
  public <Y> Root<Y> correlate (@NonNull final Root<Y> parentRoot) {
    return _query.correlate(parentRoot);
  }

  public <X, Y> Join<X, Y> correlate (@NonNull final Join<X, Y> parentJoin) {
    return _query.correlate(parentJoin);
  }

  public <X, Y> CollectionJoin<X, Y> correlate (@NonNull final CollectionJoin<X, Y> parentCollection) {
    return _query.correlate(parentCollection);
  }

  public <X, Y> SetJoin<X, Y> correlate (@NonNull final SetJoin<X, Y> parentSet) {
    return _query.correlate(parentSet);
  }

  public <X, Y> ListJoin<X, Y> correlate (@NonNull final ListJoin<X, Y> parentList) {
    return _query.correlate(parentList);
  }

  public <X, K, V> MapJoin<X, K, V> correlate (@NonNull final MapJoin<X, K, V> parentMap) {
    return _query.correlate(parentMap);
  }

  /**
   * Return the underlying subquery.
   * 
   * @return The underlying subquery.
   */
  public Subquery<Result> getSubquery () {
    return _query;
  }
}
