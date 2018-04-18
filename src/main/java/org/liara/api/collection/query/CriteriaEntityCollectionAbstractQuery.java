package org.liara.api.collection.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.springframework.lang.NonNull;

public abstract class CriteriaEntityCollectionAbstractQuery<Entity> implements EntityCollectionQuery<Entity>
{
  @NonNull
  private final EntityManager _manager;
  
  @NonNull 
  private final AbstractQuery<?> _query;
  
  @NonNull
  private final QueriedEntity<?, Entity> _entity;

  public CriteriaEntityCollectionAbstractQuery(
    @NonNull final EntityManager manager,
    @NonNull final AbstractQuery<?> query,
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
  public EntityCollectionQuery<Entity> where (@NonNull final Expression<Boolean> restriction) {
    _query.where(restriction);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity> where (@NonNull final Predicate... restrictions) {
    _query.where(restrictions);
    return this;
  }

  @Override
  public Predicate getRestriction () {
    return _query.getRestriction();
  }

  @Override
  public EntityCollectionQuery<Entity> groupBy (@NonNull final Expression<?>... grouping) {
    _query.groupBy(grouping);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity> groupBy (@NonNull final List<Expression<?>> grouping) {
    _query.groupBy(grouping);
    return this;
  }

  @Override
  public List<Expression<?>> getGroupList () {
    return _query.getGroupList();
  }

  @Override
  public EntityCollectionQuery<Entity> having (@NonNull final Expression<Boolean> restriction) {
    _query.having(restriction);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity> having (@NonNull final Predicate... restrictions) {
    _query.having(restrictions);
    return this;
  }

  @Override
  public Predicate getGroupRestriction () {
    return _query.getGroupRestriction();
  }

  @Override
  public EntityCollectionQuery<Entity> distinct (final boolean distinct) {
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
    
    return new CriteriaEntityCollectionSubquery<Joined, Result>(
        getManager(),
        subquery,
        QueriedEntity.from(from)
    );
  }
}
