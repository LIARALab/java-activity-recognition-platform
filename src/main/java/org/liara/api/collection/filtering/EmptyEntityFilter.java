package org.liara.api.collection.filtering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public class EmptyEntityFilter<Entity> implements EntityFilter<Entity>
{
  @Override
  public Predicate create (@NonNull final CriteriaBuilder builder, @NonNull final EntityCollectionQuery<Entity, ?> query) {
    return builder.conjunction();
  }
}
