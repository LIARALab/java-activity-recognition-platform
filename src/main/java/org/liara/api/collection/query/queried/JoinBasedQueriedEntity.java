package org.liara.api.collection.query.queried;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Subquery;

import org.springframework.lang.NonNull;

public class JoinBasedQueriedEntity<Base, Entity> extends FromBasedQueriedEntity<Base, Entity>
{
  @NonNull
  private final Join<Base, Entity> _join;
  
  public JoinBasedQueriedEntity(@NonNull final Join<Base, Entity> join) {
    super(join);
    _join = join;
  }

  @Override
  public QueriedEntity<Base, Entity> correlate (@NonNull final Subquery<?> subquery) {
    return new JoinBasedQueriedEntity<>(subquery.correlate(_join));
  }
}
