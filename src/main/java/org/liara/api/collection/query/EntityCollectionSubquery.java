package org.liara.api.collection.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.query.selector.EntityFieldSelector;
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
  
  /**
   * Return the underlying subquery.
   * 
   * @return The underlying subquery.
   */
  public Subquery<Result> getSubquery () {
    return _query;
  }
}
