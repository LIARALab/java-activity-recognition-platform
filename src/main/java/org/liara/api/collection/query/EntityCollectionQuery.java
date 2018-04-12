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

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.CriteriaInterpretation;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.springframework.lang.NonNull;

public interface EntityCollectionQuery<Entity, Result> extends CriteriaQuery<Result>, CompilableCriteria
{  
  public <Related> EntityCollectionQuery<Related, Result> joinCollection (@NonNull final String name);
  
  public Path<Entity> getEntity ();
  
  public <NextRelated, NextResult> EntityCollectionSubQuery<Entity, Result, NextRelated, NextResult> subquery (
    @NonNull final Class<NextRelated> relatedClass,
    @NonNull final Class<NextResult> resultClass
  );

  public default EntityCollectionQuery<Entity, Result> select (Selection<? extends Result> selection) {
    getCriteriaQuery().select(selection);
    return this;
  }

  public Path<Entity> correlateEntity (@NonNull final EntityCollectionSubQuery<Entity, ?, ?, ?> subQuery);

  public default <X> Root<X> from (Class<X> entityClass) {
    return getCriteriaQuery().from(entityClass);
  }

  public default <U> Subquery<U> subquery (Class<U> type) {
    return getCriteriaQuery().subquery(type);
  }

  public default Predicate getRestriction () {
    return getCriteriaQuery().getRestriction();
  }

  public default <X> Root<X> from (EntityType<X> entity) {
    return getCriteriaQuery().from(entity);
  }

  public default EntityCollectionQuery<Entity, Result> multiselect (Selection<?>... selections) {
    getCriteriaQuery().multiselect(selections);
    return this;
  }

  public default Set<Root<?>> getRoots () {
    return getCriteriaQuery().getRoots();
  }

  public default EntityCollectionQuery<Entity, Result> multiselect (List<Selection<?>> selectionList) {
    getCriteriaQuery().multiselect(selectionList);
    return this;
  }

  public default Selection<Result> getSelection () {
    return getCriteriaQuery().getSelection();
  }

  public default List<Expression<?>> getGroupList () {
    return getCriteriaQuery().getGroupList();
  }

  public default Predicate getGroupRestriction () {
    return getCriteriaQuery().getGroupRestriction();
  }

  public default boolean isDistinct () {
    return getCriteriaQuery().isDistinct();
  }

  public default Class<Result> getResultType () {
    return getCriteriaQuery().getResultType();
  }

  public default EntityCollectionQuery<Entity, Result> where (Expression<Boolean> restriction) {
    getCriteriaQuery().where(restriction);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> where (Predicate... restrictions) {
    getCriteriaQuery().where(restrictions);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> groupBy (Expression<?>... grouping) {
    getCriteriaQuery().groupBy(grouping);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> groupBy (List<Expression<?>> grouping) {
    getCriteriaQuery().groupBy(grouping);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> having (Expression<Boolean> restriction) {
    getCriteriaQuery().having(restriction);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> having (Predicate... restrictions) {
    getCriteriaQuery().having(restrictions);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> orderBy (Order... o) {
    getCriteriaQuery().orderBy(o);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> orderBy (List<Order> o) {
    getCriteriaQuery().orderBy(o);
    return this;
  }

  public default EntityCollectionQuery<Entity, Result> distinct (boolean distinct) {
    getCriteriaQuery().distinct(distinct);
    return this;
  }

  public default List<Order> getOrderList () {
    return getCriteriaQuery().getOrderList();
  }

  public default Set<ParameterExpression<?>> getParameters () {
    return getCriteriaQuery().getParameters();
  }
  
  public CriteriaQuery<Result> getCriteriaQuery ();
  
  public CompilableCriteria getCompilableCriteria ();

  public default void validate () {
    getCompilableCriteria().validate();
  }

  public default CriteriaInterpretation interpret (RenderingContext renderingContext) {
    return getCompilableCriteria().interpret(renderingContext);
  }
}
