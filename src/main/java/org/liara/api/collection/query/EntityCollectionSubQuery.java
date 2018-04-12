package org.liara.api.collection.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.springframework.lang.NonNull;

public interface EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> extends Subquery<RelatedResult>, EntityCollectionQuery<Related, RelatedResult>, Renderable
{
  public EntityCollectionQuery<Entity, Result> getParentQuery();
  
  public Subquery<RelatedResult> getSubQuery();

  @Override
  default Selection<RelatedResult> alias (@NonNull final String name) {
    return getSubQuery().alias(name);
  }

  @Override
  default boolean isCompoundSelection () {
    return getSubQuery().isCompoundSelection();
  }

  @Override
  default List<Selection<?>> getCompoundSelectionItems () {
    return getSubQuery().getCompoundSelectionItems();
  }

  @Override
  default Class<? extends RelatedResult> getJavaType () {
    return getSubQuery().getJavaType();
  }

  @Override
  default String getAlias () {
    return getSubQuery().getAlias();
  }

  @Override
  default <X> Root<X> from (@NonNull final Class<X> entityClass) {
    return getSubQuery().from(entityClass);
  }

  @Override
  default <X> Root<X> from (@NonNull final EntityType<X> entity) {
    return getSubQuery().from(entity);
  }

  @Override
  default Set<Root<?>> getRoots () {
    return getSubQuery().getRoots();
  }

  @Override
  default List<Expression<?>> getGroupList () {
    return getSubQuery().getGroupList();
  }

  @Override
  default Predicate getGroupRestriction () {
    return getSubQuery().getGroupRestriction();
  }

  @Override
  default boolean isDistinct () {
    return getSubQuery().isDistinct();
  }

  @Override
  default Class<RelatedResult> getResultType () {
    return getSubQuery().getResultType();
  }

  @Override
  default <U> Subquery<U> subquery (@NonNull final Class<U> type) {
    return getSubQuery().subquery(type);
  }

  @Override
  default Predicate getRestriction () {
    return getSubQuery().getRestriction();
  }

  @Override
  default Predicate isNull () {
    return getSubQuery().isNull();
  }

  @Override
  default Predicate isNotNull () {
    return getSubQuery().isNotNull();
  }

  @Override
  default Predicate in (@NonNull final Object... values) {
    return getSubQuery().in(values);
  }

  @Override
  default Predicate in (@NonNull final Expression<?>... values) {
    return getSubQuery().in(values);
  }

  @Override
  default Predicate in (@NonNull final Collection<?> values) {
    return getSubQuery().in(values);
  }

  @Override
  default Predicate in (@NonNull final Expression<Collection<?>> values) {
    return getSubQuery().in(values);
  }

  @Override
  default <X> Expression<X> as (@NonNull final Class<X> type) {
    return getSubQuery().as(type);
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> select (@NonNull final Expression<RelatedResult> expression) {
    getSubQuery().select(expression);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> where (@NonNull final Expression<Boolean> restriction) {
    getSubQuery().where(restriction);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> where (@NonNull final Predicate... restrictions) {
    getSubQuery().where(restrictions);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> groupBy (@NonNull final Expression<?>... grouping) {
    getSubQuery().groupBy(grouping);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> groupBy (@NonNull final List<Expression<?>> grouping) {
    getSubQuery().groupBy(grouping);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> having (@NonNull final Expression<Boolean> restriction) {
    getSubQuery().having(restriction);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> having (@NonNull final Predicate... restrictions) {
    getSubQuery().having(restrictions);
    return this;
  }

  @Override
  default EntityCollectionSubQuery<Entity, Result, Related, RelatedResult> distinct (final boolean distinct) {
    getSubQuery().distinct(distinct);
    return this;
  }

  @Override
  default <Y> Root<Y> correlate (@NonNull final Root<Y> parentRoot) {
    return getSubQuery().correlate(parentRoot);
  }

  @Override
  default <X, Y> Join<X, Y> correlate (@NonNull final Join<X, Y> parentJoin) {
    return getSubQuery().correlate(parentJoin);
  }

  @Override
  default <X, Y> CollectionJoin<X, Y> correlate (@NonNull final CollectionJoin<X, Y> parentCollection) {
    return getSubQuery().correlate(parentCollection);
  }

  @Override
  default <X, Y> SetJoin<X, Y> correlate (@NonNull final SetJoin<X, Y> parentSet) {
    return getSubQuery().correlate(parentSet);
  }

  @Override
  default <X, Y> ListJoin<X, Y> correlate (@NonNull final ListJoin<X, Y> parentList) {
    return getSubQuery().correlate(parentList);
  }

  @Override
  default <X, K, V> MapJoin<X, K, V> correlate (@NonNull final MapJoin<X, K, V> parentMap) {
    return getSubQuery().correlate(parentMap);
  }

  @Override
  default AbstractQuery<?> getParent () {
    return getSubQuery().getParent();
  }

  @Override
  default CommonAbstractCriteria getContainingQuery () {
    return getSubQuery().getContainingQuery();
  }

  @Override
  default Expression<RelatedResult> getSelection () {
    return getSubQuery().getSelection();
  }

  @Override
  default Set<Join<?, ?>> getCorrelatedJoins () {
    return getSubQuery().getCorrelatedJoins();
  }

  @Override
  public default CriteriaQuery<RelatedResult> getCriteriaQuery () {
    throw new UnsupportedOperationException();
  }

  @Override
  public default CompilableCriteria getCompilableCriteria () {
    throw new UnsupportedOperationException();
  }

  @Override
  default String render (@NonNull final RenderingContext renderingContext) {
    return ((Renderable) getSubQuery()).render(renderingContext);
  }
}
