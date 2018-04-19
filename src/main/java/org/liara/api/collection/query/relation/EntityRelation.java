package org.liara.api.collection.query.relation;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubquery;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface EntityRelation<Base, Joined>
{
  public void apply (
    @NonNull final EntityCollectionQuery<Base, ?> parent,
    @NonNull final EntityCollectionSubquery<Joined, Joined> children
  );
}
