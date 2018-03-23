package org.domus.api.collection;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.CriteriaInterpretation;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.springframework.lang.NonNull;

public class EntityCollectionQuery<Entity, Result> implements CriteriaQuery<Result>, CompilableCriteria
{
  @NonNull
  private final CriteriaQuery<Result> _query;
  @NonNull
  private final CompilableCriteria    _compilable;
  @NonNull
  private final Root<Entity>          _root;

  public EntityCollectionQuery(@NonNull final CriteriaQuery<Result> query, @NonNull final Class<Entity> entity) {
    this._query = query;
    this._compilable = (CompilableCriteria) query;
    this._root = query.from(entity);
  }

  public Root<Entity> getCollectionRoot () {
    return this._root;
  }

  public CriteriaQuery<Result> select (Selection<? extends Result> selection) {
    return _query.select(selection);
  }

  public <X> Root<X> from (Class<X> entityClass) {
    return _query.from(entityClass);
  }

  public <U> Subquery<U> subquery (Class<U> type) {
    return _query.subquery(type);
  }

  public Predicate getRestriction () {
    return _query.getRestriction();
  }

  public <X> Root<X> from (EntityType<X> entity) {
    return _query.from(entity);
  }

  public CriteriaQuery<Result> multiselect (Selection<?>... selections) {
    return _query.multiselect(selections);
  }

  public Set<Root<?>> getRoots () {
    return _query.getRoots();
  }

  public CriteriaQuery<Result> multiselect (List<Selection<?>> selectionList) {
    return _query.multiselect(selectionList);
  }

  public Selection<Result> getSelection () {
    return _query.getSelection();
  }

  public List<Expression<?>> getGroupList () {
    return _query.getGroupList();
  }

  public Predicate getGroupRestriction () {
    return _query.getGroupRestriction();
  }

  public boolean isDistinct () {
    return _query.isDistinct();
  }

  public Class<Result> getResultType () {
    return _query.getResultType();
  }

  public CriteriaQuery<Result> where (Expression<Boolean> restriction) {
    return _query.where(restriction);
  }

  public CriteriaQuery<Result> where (Predicate... restrictions) {
    return _query.where(restrictions);
  }

  public CriteriaQuery<Result> groupBy (Expression<?>... grouping) {
    return _query.groupBy(grouping);
  }

  public CriteriaQuery<Result> groupBy (List<Expression<?>> grouping) {
    return _query.groupBy(grouping);
  }

  public CriteriaQuery<Result> having (Expression<Boolean> restriction) {
    return _query.having(restriction);
  }

  public CriteriaQuery<Result> having (Predicate... restrictions) {
    return _query.having(restrictions);
  }

  public CriteriaQuery<Result> orderBy (Order... o) {
    return _query.orderBy(o);
  }

  public CriteriaQuery<Result> orderBy (List<Order> o) {
    return _query.orderBy(o);
  }

  public CriteriaQuery<Result> distinct (boolean distinct) {
    return _query.distinct(distinct);
  }

  public List<Order> getOrderList () {
    return _query.getOrderList();
  }

  public Set<ParameterExpression<?>> getParameters () {
    return _query.getParameters();
  }

  @Override
  public void validate () {
    this._compilable.validate();
  }

  @Override
  public CriteriaInterpretation interpret (RenderingContext renderingContext) {
    return this._compilable.interpret(renderingContext);
  }
}
