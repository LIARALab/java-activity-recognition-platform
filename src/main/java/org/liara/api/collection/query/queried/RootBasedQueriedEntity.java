package org.liara.api.collection.query.queried;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.lang.NonNull;

public class RootBasedQueriedEntity<Entity> extends FromBasedQueriedEntity<Entity, Entity>
{
  @NonNull
  private final Root<Entity> _root;
  
  public RootBasedQueriedEntity(@NonNull final Root<Entity> root) {
    super(root);
    _root = root;
  }

  @Override
  public QueriedEntity<Entity, Entity> correlate (@NonNull final Subquery<?> subquery) {
    return new RootBasedQueriedEntity<>(subquery.correlate(_root));
  }
}
