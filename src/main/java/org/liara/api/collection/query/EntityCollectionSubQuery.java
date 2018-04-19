package org.liara.api.collection.query;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.queried.QueriedEntity;
import org.springframework.lang.NonNull;

public interface EntityCollectionSubquery<Entity, Result>
      extends EntityCollectionQuery<Entity, Result>, Expression<Result>
{ 
  public <Related> QueriedEntity<?, Related> correlate (@NonNull final QueriedEntity<?, Related> related);
}
