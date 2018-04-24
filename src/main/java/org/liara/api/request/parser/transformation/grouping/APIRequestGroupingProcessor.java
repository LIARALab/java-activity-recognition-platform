package org.liara.api.request.parser.transformation.grouping;

import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;

public interface APIRequestGroupingProcessor<Entity>
{
  public EntityCollectionGroupTransformation<Entity> process (
    @NonNull final String key
  );
  
  public boolean hableToProcess (
    @NonNull final String key
  );
}
