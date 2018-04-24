package org.liara.api.request.parser.operator.ordering;

import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.springframework.lang.NonNull;

public interface APIRequestOrderingProcessor<Entity>
{
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  );
  
  public boolean hableToProcess (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  );
}
