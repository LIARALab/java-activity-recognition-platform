package org.liara.api.collection.ordering;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public interface Ordering<Entity>
{
  public default void order (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    query.orderBy(create(builder, query));
  }
  
  public List<Order> create (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  );
}
