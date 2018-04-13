package org.liara.api.request.parser.grouping;

import org.liara.api.collection.grouping.EntityGrouping;
import org.liara.api.collection.grouping.ExpressionGrouping;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public class APIRequestExpressionGroupingProcessor<Entity> implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final String _key;
  
  @NonNull
  private final SimplifiedCriteriaExpressionSelector<?> _selector;
  
  public APIRequestExpressionGroupingProcessor (
    @NonNull final String key, 
    @NonNull final SimplifiedCriteriaExpressionSelector<?> selector
  ) {
    _key = key;
    _selector = selector;
  }

  @Override
  public EntityGrouping<Entity> process (@NonNull final String key) {
    return new ExpressionGrouping<>(_selector);
  }

  @Override
  public boolean hableToProcess (@NonNull final String key) {
    return key.equals(_key);
  }
}
