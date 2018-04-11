package org.liara.api.request.parser.ordering;

import org.liara.api.collection.ordering.Ordering;
import org.liara.api.collection.ordering.OrderingDirection;
import org.springframework.lang.NonNull;

public interface APIRequestOrderingProcessor<Entity>
{
  public Ordering<Entity> process (@NonNull final String key, @NonNull final OrderingDirection direction);
  
  public boolean hableToProcess (@NonNull final String key, @NonNull final OrderingDirection direction);
}
