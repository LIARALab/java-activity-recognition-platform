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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.cursor.Cursor;
import org.springframework.lang.NonNull;

public class EntityCollectionMainQuery<Entity, Output> extends AbstractEntityCollectionQuery<Entity, Output>
{
  @NonNull 
  private final CriteriaQuery<Output> _query;
  
  public EntityCollectionMainQuery (
    @NonNull final EntityManager manager,
    @NonNull final CriteriaQuery<Output> query,
    @NonNull final QueriedEntity<?, Entity> entity
  ) {
    super(manager, query, entity);
    _query = query;
  }
  
  public EntityCollectionMainQuery<Entity, Output> select (
    @NonNull final Selection<? extends Output> selection
  ) {
    _query.select(selection);
    return this;
  }

  @Override
  public EntityCollectionMainQuery<Entity, Output> orderBy (@NonNull final Order... o) {
    _query.orderBy(o);
    return this;
  }

  @Override
  public EntityCollectionMainQuery<Entity, Output> orderBy (@NonNull final List<Order> o) {
    _query.orderBy(o);
    return this;
  }

  @Override
  public List<Order> getOrderList () {
    return _query.getOrderList();
  }

  @Override
  public Set<ParameterExpression<?>> getParameters () {
    return _query.getParameters();
  }

  @Override
  public <Joined> EntityCollectionMainQuery<Joined, Output> join (@NonNull final Join<Entity, Joined> join) {
    return new EntityCollectionMainQuery<>(
        getManager(), _query, QueriedEntity.from(join)
    );
  }
  
  @Override
  public <Joined> EntityCollectionMainQuery<Joined, Output> join (
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join
  ) {
    return (EntityCollectionMainQuery<Joined, Output>) super.join(join);
  }

  @Override
  public <Joined> EntityCollectionMainQuery<Joined, Output> join (
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join
  ) {
    return join((EntityFieldSelector<Entity, Join<Entity, Joined>>) join);
  }
  
  public List<Output> fetchAllAndClose () {
    final List<Output> result = fetchAll();

    getManager().close();
    
    return result;
  }
  
  public Optional<Output> fetchFirstAndClose () {
    final Optional<Output> result = fetchFirst();
    
    getManager().close();
    
    return result;
  }
  
  public Optional<Output> fetchOneAndClose (final int index) {
    final Optional<Output> result = fetchOne(index);
    
    getManager().close();
    
    return result;
  }
  
  public List<Output> fetchCursorAndClose (@NonNull final Cursor cursor) {
    final List<Output> result = fetchCursor(cursor);
    
    getManager().close();
    
    return result;
  }
  

  
  public List<Output> fetchAll () {
    final List<Output> result = getManager().createQuery(_query)
                                            .getResultList();
    
    return result;
  }
  
  public Optional<Output> fetchFirst () {
    final List<Output> result = getManager().createQuery(_query)
                                            .setMaxResults(1)
                                            .getResultList();
    
    if (result.size() > 0) {
      return Optional.ofNullable(result.get(0));
    } else {
      return Optional.empty();
    }
  }
  
  public Optional<Output> fetchOne (final int index) {
    final List<Output> result = getManager().createQuery(_query)
                                            .setMaxResults(1)
                                            .setFirstResult(index)
                                            .getResultList();
    
    if (result.size() > 0) {
      return Optional.ofNullable(result.get(0));
    } else {
      return Optional.empty();
    }
  }
  
  public List<Output> fetchCursor (@NonNull final Cursor cursor) {
    final TypedQuery<Output> query = getManager().createQuery(_query);
   
    query.setFirstResult(cursor.getOffset());
    
    if (cursor.hasLimit()) {
      query.setMaxResults(cursor.getLimit());
    }
    
    final List<Output> result = query.getResultList();
    
    return result;
  }

  public Selection<?> getSelection () {
    return _query.getSelection();
  }

  public EntityCollectionMainQuery<Entity, Output> multiselect (@NonNull final List<Selection<?>> selections) {
    _query.multiselect(selections);
    return this;
  }
  
  public EntityCollectionMainQuery<Entity, Output> multiselect (@NonNull final Selection<?> ...selections) {
    _query.multiselect(selections);
    return this;
  }
}
