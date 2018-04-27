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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

/**
 * A query over a colllection of a particular entity.
 * 
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> The targeted entity.
 */
public interface EntityCollectionQuery<Entity, Output>
{   
  /**
   * Wrap the given query into an EntityCollectionQuery.
   * 
   * @param manager
   * @param query
   * @param from
   * @return
   */
  public static <Entity, Result> EntityCollectionMainQuery<Entity, Result> from (
    @NonNull final EntityManager manager,
    @NonNull final CriteriaQuery<Result> query, 
    @NonNull final Root<Entity> entity
  ) {
    return new EntityCollectionMainQuery<>(manager, query, QueriedEntity.from(entity));
  }

  /**
   * Wrap the given query into an EntityCollectionQuery.
   * 
   * @param manager
   * @param query
   * @param entity
   * @return
   */
  public static <Entity, Result> EntityCollectionSubquery<Entity, Result> from (
    @NonNull final EntityManager manager, 
    @NonNull final Subquery<Result> query, 
    @NonNull final Root<Entity> entity
  ) {
    return new EntityCollectionSubquery<>(manager, query, QueriedEntity.from(entity));
  }
  
  /**
   * Return the type of the queried entity.
   * 
   * @return The type of the queried entity.
   */
  public default Class<Entity> getEntityType () {
    return getEntity().getModel().getBindableJavaType();
  }
  
  /**
   * Return the queried entity.
   * 
   * @return The queried entity.
   */
  public QueriedEntity<?, Entity> getEntity ();
  
  /**
   * Return this query's related entity manager instance.
   * 
   * @return This query's related entity manager instance.
   */
  public EntityManager getManager ();

  /**
   * @see AbstractQuery#from(Class)
   */
  public <X> Root<X> from (@NonNull final Class<X> entityClass);

  /**
   * @see AbstractQuery#from(EntityType)
   */
  public <X> Root<X> from (@NonNull final EntityType<X> entity);

  /**
   * @see AbstractQuery#getRoots()
   */
  public Set<Root<?>> getRoots ();

  /**
   * @see AbstractQuery#where(Expression)
   */
  public EntityCollectionQuery<Entity, Output> where (@NonNull final Expression<Boolean> restriction);
  
  /**
   * Create an EntityCollectionQuery on the given join.
   * 
   * @param join Join to apply.
   * 
   * @return An EntityCollectionQuery on the given join.
   */
  public <Joined> EntityCollectionQuery<Joined, Output> join (@NonNull final Join<Entity, Joined> join);
  
  /**
   * Create an EntityCollectionQuery on the given join.
   * 
   * @param join Join to apply.
   * 
   * @return An EntityCollectionQuery on the given join.
   */
  public <Joined> EntityCollectionQuery<Joined, Output> join (@NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join);
  
  /**
   * Create an EntityCollectionQuery on the given join.
   * 
   * @param join Join to apply.
   * 
   * @return An EntityCollectionQuery on the given join.
   */
  public <Joined> EntityCollectionQuery<Joined, Output> join (@NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join);
  
  /**
   * Create a subquery.
   * 
   * @param joined Target entity of the subquery.
   * @param result Result type of the subquery.
   * 
   * @return A sub query for the given configuration.
   */
  public <Joined, Result> EntityCollectionSubquery<Joined, Result> subquery (
      @NonNull final Class<Joined> joined,
      @NonNull final Class<Result> result
  );

  /**
   * Allow to chain restrictions with a and criteria.
   * 
   * If this query does not have any restriction yet, a call to andWhere will be the same
   * as a call to where.
   * 
   * @param restriction Restriction to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> andWhere (@NonNull final Expression<Boolean> restriction) {
    final Predicate current = getRestriction();
    
    if (current == null) {
      return where(restriction);
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return where(builder.and(current, restriction));
    }    
  }
  
  /**
   * Allow to chain restrictions with a or criteria.
   * 
   * If this query does not have any restriction yet, a call to orWhere will do nothing.
   * 
   * @param restriction Restriction to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> orWhere (@NonNull final Expression<Boolean> restriction) {
    final Predicate current = getRestriction();
    
    if (current == null) {
      return this;
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return where(builder.or(current, restriction));
    }
  }

  /**
   * @see AbstractQuery#where(Predicate...)
   */
  public EntityCollectionQuery<Entity, Output> where (@NonNull final Predicate... restrictions);
  
  /**
   * Allow to chain restrictions with a and criteria.
   * 
   * If this query does not have any restriction yet, a call to andWhere will be the same
   * as a call to where.
   * 
   * @param restrictions Restrictions to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> andWhere (@NonNull final Predicate... restrictions) {
    final Predicate current = getRestriction();
    
    if (current == null) {
      return where(restrictions);
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return where(builder.and(current, builder.and(restrictions)));
    }
  }
  
  /**
   * Allow to chain restrictions with a or criteria.
   * 
   * If this query does not have any restriction yet, a call to orWhere will do nothing.
   * 
   * @param restrictions Restrictions to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> orWhere (@NonNull final Predicate... restrictions) {
    final Predicate current = getRestriction();
    
    if (current == null) {
      return this;
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return this.where(builder.or(current, builder.or(restrictions)));
    }
  }
  
  /**
   * @see AbstractQuery#getRestriction()
   */
  public Predicate getRestriction ();

  /**
   * @see AbstractQuery#groupBy(Expression...)
   */
  public EntityCollectionQuery<Entity, Output> groupBy (@NonNull final Expression<?>... grouping);
  
  /**
   * Allow you to chain group by clauses.
   * 
   * @param grouping The group by clauses to add to the previous ones.
   * 
   * @return The current query instance.
   */
  public default EntityCollectionQuery<Entity, Output> andGroupBy (@NonNull final Expression<?>... grouping) {
    final List<Expression<?>> groups = new ArrayList<>(getGroupList());
    groups.addAll(Arrays.asList(grouping));
    
    return this.groupBy(groups);
  }

  /**
   * @see AbstractQuery#groupBy(List)
   */
  public EntityCollectionQuery<Entity, Output> groupBy (@NonNull final List<Expression<?>> grouping);
  
  /**
   * Allow you to chain group by clauses.
   * 
   * @param grouping The group by clauses to add to the previous ones.
   * 
   * @return The current query instance.
   */
  public default EntityCollectionQuery<Entity, Output> andGroupBy (@NonNull final List<Expression<?>> grouping) {
    final List<Expression<?>> groups = new ArrayList<>(getGroupList());
    groups.addAll(grouping);
    
    return this.groupBy(groups);
  }
  
  /**
   * @see AbstractQuery#getGroupList()
   */
  public List<Expression<?>> getGroupList ();

  /**
   * @see AbstractQuery#having(Expression)
   */
  public EntityCollectionQuery<Entity, Output> having (@NonNull final Expression<Boolean> restriction);

  /**
   * Allows you to chain group restrictions with a and criteria.
   * 
   * If this query does not have any group restriction yet, a call to andHaving will do the same
   * action as a call to having.
   * 
   * @param restriction Restriction to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> andHaving (@NonNull final Expression<Boolean> restriction) {
    final Predicate current = getGroupRestriction();
    
    if (current == null) {
      return having(restriction);
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return having(builder.and(current, restriction));
    }    
  }
  
  /**
   * Allows you to chain group restrictions with a or criteria.
   * 
   * If this query does not have any group restriction yet, a call to orHaving will do nothing.
   * 
   * @param restriction Restriction to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> orHaving (@NonNull final Expression<Boolean> restriction) {
    final Predicate current = getGroupRestriction();
    
    if (current == null) {
      return this;
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return having(builder.or(current, restriction));
    }
  }

  /**
   * @see AbstractQuery#having(Predicate...)
   */
  public EntityCollectionQuery<Entity, Output> having (@NonNull final Predicate... restrictions);

  /**
   * Allows you to chain group restrictions with a and criteria.
   * 
   * If this query does not have any group restriction yet, a call to andHaving will do the same
   * action as a call to having.
   * 
   * @param restrictions Restrictions to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> andHaving (@NonNull final Predicate... restrictions) {
    final Predicate current = getGroupRestriction();
    
    if (current == null) {
      return having(restrictions);
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return having(builder.and(current, builder.and(restrictions)));
    }    
  }

  /**
   * Allows you to chain group restrictions with a or criteria.
   * 
   * If this query does not have any group restriction yet, a call to orHaving will do nothing.
   * 
   * @param restrictions Restrictions to chain.
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> orHaving (@NonNull final Predicate... restrictions) {
    final Predicate current = getGroupRestriction();
    
    if (current == null) {
      return this;
    } else {
      final CriteriaBuilder builder = getManager().getCriteriaBuilder();
      return having(builder.or(current, builder.or(restrictions)));
    }
  }

  /**
   * @see AbstractQuery#getGroupRestriction()
   */
  public Predicate getGroupRestriction ();

  /**
   * If the underlying query can't be ordered, a call to this method will do nothing.
   * 
   * @see CriteriaQuery#orderBy(Order...)
   */
  public EntityCollectionQuery<Entity, Output> orderBy (@NonNull final Order... o);
  
  /**
   * Allow you to chain ordering clauses.
   * 
   * If the underlying query can't be ordered, a call to this method will do nothing.
   * 
   * @param o New ordering clauses to add to the previous ones.
   * 
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> andOrderBy (@NonNull final Order... o) {
    final List<Order> orders = new ArrayList<>(getOrderList());
    orders.addAll(Arrays.asList(o));
    
    return orderBy(orders);
  }

  /**
   * If the underlying query can't be ordered, a call to this method will do nothing.
   * 
   * @see CriteriaQuery#orderBy(List)
   */
  public EntityCollectionQuery<Entity, Output> orderBy (@NonNull final List<Order> o);

  /**
   * Allow you to chain ordering clauses.
   * 
   * If the underlying query can't be ordered, a call to this method will do nothing.
   * 
   * @param o New ordering clauses to add to the previous ones.
   * 
   * @return The current query.
   */
  public default EntityCollectionQuery<Entity, Output> andOrderBy (@NonNull final List<Order> o) {
    final List<Order> orders = new ArrayList<>(getOrderList());
    orders.addAll(o);
    
    return orderBy(orders);
  }
  
  /**
   * @see CriteriaQuery#getOrderList()
   */
  public List<Order> getOrderList ();
  
  /**
   * @see AbstractQuery#distinct(boolean)
   */
  public EntityCollectionQuery<Entity, Output> distinct (final boolean distinct);

  /**
   * @see AbstractQuery#isDistinct()
   */
  public boolean isDistinct ();

  /**
   * @see AbstractQuery#getParameters()
   */
  public Set<ParameterExpression<?>> getParameters ();
}
