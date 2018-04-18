package org.liara.api.collection.query;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.springframework.lang.NonNull;

public class CriteriaEntityCollectionQuery<Entity> extends CriteriaEntityCollectionAbstractQuery<Entity>
{
  @NonNull 
  private final CriteriaQuery<?> _query;
  
  public CriteriaEntityCollectionQuery (
    @NonNull final EntityManager manager,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final QueriedEntity<?, Entity> entity
  ) {
    super(manager, query, entity);
    _query = query;
  }

  @Override
  public EntityCollectionQuery<Entity> orderBy (@NonNull final Order... o) {
    _query.orderBy(o);
    return this;
  }

  @Override
  public EntityCollectionQuery<Entity> orderBy (@NonNull final List<Order> o) {
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
  public <Joined> EntityCollectionQuery<Joined> join (@NonNull final Join<Entity, Joined> join) {
    return new CriteriaEntityCollectionQuery<>(getManager(), _query, QueriedEntity.from(join));
  }
}
