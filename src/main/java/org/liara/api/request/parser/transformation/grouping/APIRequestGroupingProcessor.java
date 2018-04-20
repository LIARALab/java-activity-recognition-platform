package org.liara.api.request.parser.transformation.grouping;

import javax.persistence.Tuple;

import org.liara.api.collection.transformation.EntityCollectionTransformation;
import org.springframework.lang.NonNull;

public interface APIRequestGroupingProcessor<Entity>
{
  public EntityCollectionTransformation<Entity, Tuple> process (
    @NonNull final String key
  );
  
  public boolean hableToProcess (
    @NonNull final String key
  );
}
