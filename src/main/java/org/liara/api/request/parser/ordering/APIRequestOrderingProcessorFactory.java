package org.liara.api.request.parser.ordering;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public final class APIRequestOrderingProcessorFactory
{
  public static <Entity> APIRequestOrderingProcessor<Entity> field (
    @NonNull final String key, 
    @NonNull final SimplifiedCriteriaExpressionSelector<?> selector
  ) {
    return field(key, (CriteriaExpressionSelector<?>) selector);
  }
  
  public static <Entity> APIRequestOrderingProcessor<Entity> field (
    @NonNull final String key, 
    @NonNull final CriteriaExpressionSelector<?> selector
  ) {
    return new APIRequestFieldOrderingProcessor<>(key, selector);
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> join (
    @NonNull final String alias, 
    @NonNull final String join, 
    @NonNull final CollectionRequestConfiguration<Joined> configuration
  ) {
    return new APIRequestJoinOrderingProcessor<>(join, alias, configuration);
  }
}
