package org.liara.api.collection.query.queried;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.lang.NonNull;

public abstract class FromBasedQueriedEntity<Base, Entity> implements QueriedEntity<Base, Entity>
{
  @NonNull
  private final From<Base, Entity> _from;
  
  public FromBasedQueriedEntity(@NonNull final From<Base, Entity> from) {
    _from = from;
  }

  @Override
  public Predicate isNull () {
    return _from.isNull();
  }

  @Override
  public Class<? extends Entity> getJavaType () {
    return _from.getJavaType();
  }

  @Override
  public Selection<Entity> alias (@NonNull final String name) {
    return _from.alias(name);
  }

  @Override
  public Set<Fetch<Entity, ?>> getFetches () {
    return _from.getFetches();
  }

  @Override
  public Predicate isNotNull () {
    return _from.isNotNull();
  }

  @Override
  public String getAlias () {
    return _from.getAlias();
  }

  @Override
  public Bindable<Entity> getModel () {
    return _from.getModel();
  }

  @Override
  public Predicate in (@NonNull final Object... values) {
    return _from.in(values);
  }

  @Override
  public boolean isCompoundSelection () {
    return _from.isCompoundSelection();
  }

  @Override
  public Path<?> getParentPath () {
    return _from.getParentPath();
  }

  @Override
  public <Y> Fetch<Entity, Y> fetch (@NonNull final SingularAttribute<? super Entity, Y> attribute) {
    return _from.fetch(attribute);
  }

  @Override
  public List<Selection<?>> getCompoundSelectionItems () {
    return _from.getCompoundSelectionItems();
  }

  @Override
  public <Y> Path<Y> get (@NonNull final SingularAttribute<? super Entity, Y> attribute) {
    return _from.get(attribute);
  }

  @Override
  public Predicate in (@NonNull final Expression<?>... values) {
    return _from.in(values);
  }

  @Override
  public Set<Join<Entity, ?>> getJoins () {
    return _from.getJoins();
  }

  @Override
  public <Y> Fetch<Entity, Y> fetch (
    @NonNull final SingularAttribute<? super Entity, Y> attribute, 
    @NonNull final JoinType jt
  ) {
    return _from.fetch(attribute, jt);
  }

  @Override
  public <E, C extends Collection<E>> Expression<C> get (@NonNull final PluralAttribute<Entity, C, E> collection) {
    return _from.get(collection);
  }

  @Override
  public Predicate in (@NonNull final Collection<?> values) {
    return _from.in(values);
  }

  @Override
  public boolean isCorrelated () {
    return _from.isCorrelated();
  }

  @Override
  public <Y> Fetch<Entity, Y> fetch (@NonNull final PluralAttribute<? super Entity, ?, Y> attribute) {
    return _from.fetch(attribute);
  }

  @Override
  public Predicate in (@NonNull final Expression<Collection<?>> values) {
    return _from.in(values);
  }

  @Override
  public <K, V, M extends Map<K, V>> Expression<M> get (@NonNull final MapAttribute<Entity, K, V> map) {
    return _from.get(map);
  }

  @Override
  public From<Base, Entity> getCorrelationParent () {
    return _from.getCorrelationParent();
  }

  @Override
  public <Y> Fetch<Entity, Y> fetch (@NonNull final PluralAttribute<? super Entity, ?, Y> attribute, @NonNull final JoinType jt) {
    return _from.fetch(attribute, jt);
  }

  @Override
  public <X> Expression<X> as (@NonNull final Class<X> type) {
    return _from.as(type);
  }

  @Override
  public Expression<Class<? extends Entity>> type () {
    return _from.type();
  }

  @Override
  public <X, Y> Fetch<X, Y> fetch (@NonNull final String attributeName) {
    return _from.fetch(attributeName);
  }

  @Override
  public <Y> Join<Entity, Y> join (@NonNull final SingularAttribute<? super Entity, Y> attribute) {
    return _from.join(attribute);
  }

  @Override
  public <Y> Path<Y> get (@NonNull final String attributeName) {
    return _from.get(attributeName);
  }

  @Override
  public <Y> Join<Entity, Y> join (@NonNull final SingularAttribute<? super Entity, Y> attribute, @NonNull final JoinType jt) {
    return _from.join(attribute, jt);
  }

  @Override
  public <X, Y> Fetch<X, Y> fetch (@NonNull final String attributeName, @NonNull final JoinType jt) {
    return _from.fetch(attributeName, jt);
  }

  @Override
  public <Y> CollectionJoin<Entity, Y> join (@NonNull final CollectionAttribute<? super Entity, Y> collection) {
    return _from.join(collection);
  }

  @Override
  public <Y> SetJoin<Entity, Y> join (@NonNull final SetAttribute<? super Entity, Y> set) {
    return _from.join(set);
  }

  @Override
  public <Y> ListJoin<Entity, Y> join (@NonNull final ListAttribute<? super Entity, Y> list) {
    return _from.join(list);
  }

  @Override
  public <K, V> MapJoin<Entity, K, V> join (@NonNull final MapAttribute<? super Entity, K, V> map) {
    return _from.join(map);
  }

  @Override
  public <Y> CollectionJoin<Entity, Y> join (@NonNull final CollectionAttribute<? super Entity, Y> collection, @NonNull final JoinType jt) {
    return _from.join(collection, jt);
  }

  @Override
  public <Y> SetJoin<Entity, Y> join (@NonNull final SetAttribute<? super Entity, Y> set, @NonNull final JoinType jt) {
    return _from.join(set, jt);
  }

  @Override
  public <Y> ListJoin<Entity, Y> join (@NonNull final ListAttribute<? super Entity, Y> list, @NonNull final JoinType jt) {
    return _from.join(list, jt);
  }

  @Override
  public <K, V> MapJoin<Entity, K, V> join (@NonNull final MapAttribute<? super Entity, K, V> map, @NonNull final JoinType jt) {
    return _from.join(map, jt);
  }

  @Override
  public <X, Y> Join<X, Y> join (@NonNull final String attributeName) {
    return _from.join(attributeName);
  }

  @Override
  public <X, Y> CollectionJoin<X, Y> joinCollection (@NonNull final String attributeName) {
    return _from.joinCollection(attributeName);
  }

  @Override
  public <X, Y> SetJoin<X, Y> joinSet (@NonNull final String attributeName) {
    return _from.joinSet(attributeName);
  }

  @Override
  public <X, Y> ListJoin<X, Y> joinList (@NonNull final String attributeName) {
    return _from.joinList(attributeName);
  }

  @Override
  public <X, K, V> MapJoin<X, K, V> joinMap (@NonNull final String attributeName) {
    return _from.joinMap(attributeName);
  }

  @Override
  public <X, Y> Join<X, Y> join (@NonNull final String attributeName, @NonNull final JoinType jt) {
    return _from.join(attributeName, jt);
  }

  @Override
  public <X, Y> CollectionJoin<X, Y> joinCollection (@NonNull final String attributeName, @NonNull final JoinType jt) {
    return _from.joinCollection(attributeName, jt);
  }

  @Override
  public <X, Y> SetJoin<X, Y> joinSet (@NonNull final String attributeName, @NonNull final JoinType jt) {
    return _from.joinSet(attributeName, jt);
  }

  @Override
  public <X, Y> ListJoin<X, Y> joinList (@NonNull final String attributeName, @NonNull final JoinType jt) {
    return _from.joinList(attributeName, jt);
  }

  @Override
  public <X, K, V> MapJoin<X, K, V> joinMap (@NonNull final String attributeName, @NonNull final JoinType jt) {
    return _from.joinMap(attributeName, jt);
  }
}
