package org.liara.api.request.parser.ordering;

import org.liara.api.collection.ordering.ExpressionOrdering;
import org.liara.api.collection.ordering.Ordering;
import org.liara.api.collection.ordering.OrderingDirection;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public class APIRequestFieldOrderingProcessor<Entity> implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final String _key;
  
  @NonNull
  private final CriteriaExpressionSelector<?> _selector;
  
  public APIRequestFieldOrderingProcessor (
    @NonNull final String key, 
    @NonNull final CriteriaExpressionSelector<?> selector
  ) {
    _key = key;
    _selector = selector;
  }

  @Override
  public Ordering<Entity> process (@NonNull final String key, @NonNull final OrderingDirection direction) {
    return new ExpressionOrdering<>(_selector, direction);
  }

  @Override
  public boolean hableToProcess (@NonNull final String key, @NonNull final OrderingDirection direction) {
    return key.equals(_key);
  }
}
