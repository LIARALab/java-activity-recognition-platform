package org.liara.api.collection.query.selector;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.queried.QueriedEntity;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface EntityFieldSelector<Entity, Selected>
{
  default public Selected select (
    @NonNull final EntityCollectionQuery<Entity> query
  ) {
    return select(query, query.getEntity());
  }
  
  public Selected select (
    @NonNull final EntityCollectionQuery<?> query,
    @NonNull final QueriedEntity<?, Entity> entity
  );
}
