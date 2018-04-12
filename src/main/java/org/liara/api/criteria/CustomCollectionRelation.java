package org.liara.api.criteria;

import javax.persistence.criteria.CriteriaBuilder;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubQuery;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface CustomCollectionRelation<Entity, Joined>
{
  public void apply (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> master,
    @NonNull final EntityCollectionSubQuery<Entity, ?, Joined, ?> slave
  );
}
