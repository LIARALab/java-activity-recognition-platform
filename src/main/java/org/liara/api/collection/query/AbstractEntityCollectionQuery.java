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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

public abstract class AbstractEntityCollectionQuery<Entity, Output> implements EntityCollectionQuery<Entity, Output>
{
  @NonNull
  private final EntityManager _manager;
  
  @NonNull 
  private final AbstractQuery<Output> _query;
  
  @NonNull
  private final QueriedEntity<?, Entity> _entity;
  
  @NonNull
  private final Map<
    EntityFieldSelector<Entity, ?>,
    EntityCollectionQuery<?, Output>
  > _joins = new HashMap<>();

  public AbstractEntityCollectionQuery(
    @NonNull final EntityManager manager,
    @NonNull final AbstractQuery<Output> query,
    @NonNull final QueriedEntity<?, Entity> entity
  )
  {
    _manager = manager;
    _query = query;
    _entity = entity;
  }
  
  @Override
  public QueriedEntity<?, Entity> getEntity () {
    return _entity;
  }

  @Override
  public EntityManager getManager () {
    return _manager;
  }

  @Override
  public <X> Root<X> from (@NonNull final Class<X> entityClass) {
    return _query.from(entityClass);
  }

  @Override
  public <X> Root<X> from (@NonNull final EntityType<X> entity) {
    return _query.from(entity);
  }

  @Override
  public Set<Root<?>> getRoots () {
    return _query.getRoots();
  }

  @Override
  public EntityCollectionQuery<Entity, Output> where (@NonNull final Expression<Boolean> restriction) {
    _query.where(restriction);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity, Output> where (@NonNull final Predicate... restrictions) {
    _query.where(restrictions);
    return this;
  }

  @Override
  public Predicate getRestriction () {
    return _query.getRestriction();
  }

  @Override
  public EntityCollectionQuery<Entity, Output> groupBy (@NonNull final Expression<?>... grouping) {
    _query.groupBy(grouping);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity, Output> groupBy (@NonNull final List<Expression<?>> grouping) {
    _query.groupBy(grouping);
    return this;
  }

  @Override
  public List<Expression<?>> getGroupList () {
    return _query.getGroupList();
  }

  @Override
  public EntityCollectionQuery<Entity, Output> having (@NonNull final Expression<Boolean> restriction) {
    _query.having(restriction);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity, Output> having (@NonNull final Predicate... restrictions) {
    _query.having(restrictions);
    return this;
  }

  @Override
  public Predicate getGroupRestriction () {
    return _query.getGroupRestriction();
  }

  @Override
  public EntityCollectionQuery<Entity, Output> distinct (final boolean distinct) {
    _query.distinct(distinct);
    return this;
  }

  @Override
  public boolean isDistinct () {
    return _query.isDistinct();
  }

  @Override
  public <Joined, Result> EntityCollectionSubquery<Joined, Result> subquery (
    @NonNull final Class<Joined> joined, 
    @NonNull final Class<Result> result
  ) {
    final Subquery<Result> subquery = _query.subquery(result);
    final Root<Joined> from = subquery.from(joined);
    
    return new EntityCollectionSubquery<Joined, Result>(
        getManager(),
        subquery,
        QueriedEntity.from(from)
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Joined> EntityCollectionQuery<Joined, Output> join (
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join
  ) {
    if (!_joins.containsKey(join)) {
      _joins.put(join, join(join.select(this)));
    }
    
    return (EntityCollectionQuery<Joined, Output>) _joins.get(join);
  }
  
  @Override
  public <Joined> EntityCollectionQuery<Joined, Output> join (
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join
  ) {
    return join((EntityFieldSelector<Entity, Join<Entity, Joined>>) join);
  }
}