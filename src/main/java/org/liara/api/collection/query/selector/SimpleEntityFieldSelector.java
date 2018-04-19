package org.liara.api.collection.query.selector;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.queried.QueriedEntity;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface SimpleEntityFieldSelector<Entity, Selected>
       extends EntityFieldSelector<Entity, Selected>
{
  public Selected select (@NonNull final QueriedEntity<?, Entity> entity);
  
  public default Selected select (
    @NonNull final EntityCollectionQuery<?, ?> query,
    @NonNull final QueriedEntity<?, Entity> entity
  ) {
    return select(entity);
  }
}
