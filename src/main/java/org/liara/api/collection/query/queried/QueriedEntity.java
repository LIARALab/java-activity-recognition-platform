package org.liara.api.collection.query.queried;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.lang.NonNull;

public interface QueriedEntity<Base, Entity> extends From<Base, Entity>
{
  public static <Entity> QueriedEntity<Entity, Entity> from (@NonNull final Root<Entity> root) {
    return new RootBasedQueriedEntity<>(root);
  }
  
  public static <Base, Entity> QueriedEntity<Base, Entity> from (@NonNull final Join<Base, Entity> join) {
    return new JoinBasedQueriedEntity<>(join);
  }
  
  public QueriedEntity<Base, Entity> correlate (@NonNull final Subquery<?> subquery);
}
