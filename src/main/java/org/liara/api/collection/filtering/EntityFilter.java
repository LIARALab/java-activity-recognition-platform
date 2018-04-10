package org.liara.api.collection.filtering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public interface EntityFilter<Entity>
{
  public Predicate create (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  );

  public default <Result> EntityCollectionQuery<Entity, Result> filter (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, Result> query
  ) {
    return query.where(create(builder, query));
  }
}
