package org.liara.api.collection.transformation;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.view.EntityCollectionQueryBasedView;
import org.springframework.lang.NonNull;

public interface EntityCollectionQueryTransformation<Entity>
       extends   Transformation<
                   EntityCollectionQueryBasedView<Entity, ?, ?>,
                   EntityCollectionQueryBasedView<Entity, ?, ?>
                 >
{
  public <Result> EntityCollectionMainQuery<Entity, Result> createCollectionQuery (
    @NonNull final EntityCollectionQueryBasedView<Entity, ?, ?> base,
    @NonNull final Class<Result> result
  );
}
