package org.liara.api.collection.query;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.springframework.lang.NonNull;

public interface EntityCollectionSubquery<Entity, Result>
      extends EntityCollectionQuery<Entity>, Expression<Result>
{ 
  public <Entity> QueriedEntity<?, Entity> correlate (@NonNull final QueriedEntity<?, Entity> entity);
}
