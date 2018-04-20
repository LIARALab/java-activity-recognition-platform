package org.liara.api.request.parser.transformation.grouping;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public final class APIRequestGroupingProcessorFactory
{
  public static <Entity> APIRequestGroupingProcessor<Entity> expression (
    @NonNull final String key, 
    @NonNull final CriteriaExpressionSelector<?> selector
  ) {
    return expression(key, (SimplifiedCriteriaExpressionSelector<?>) selector);
  }
  
  public static <Entity> APIRequestGroupingProcessor<Entity> expression (
    @NonNull final String key, 
    @NonNull final SimplifiedCriteriaExpressionSelector<?> selector
  ) {
    return new APIRequestExpressionGroupingProcessor<>(key, selector);
  }
  
  public static <Entity, Joined> APIRequestGroupingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final String join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestJoinGroupingProcessor<>(join, alias, configuration);
  }
  
  public static <Entity, Joined> APIRequestGroupingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final String join, 
    @NonNull final Class<? extends EntityCollection<Joined, ?>> configuration
  ) {
    return new APIRequestJoinGroupingProcessor<>(join, alias, CollectionRequestConfiguration.getDefaultClass(configuration));
  }
}
