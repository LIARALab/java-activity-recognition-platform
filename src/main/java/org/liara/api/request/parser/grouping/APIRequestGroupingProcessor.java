package org.liara.api.request.parser.grouping;

import org.liara.api.collection.grouping.EntityGrouping;
import org.springframework.lang.NonNull;

public interface APIRequestGroupingProcessor<Entity>
{
  public EntityGrouping<Entity> process (@NonNull final String key);
  
  public boolean hableToProcess (@NonNull final String key);
}
