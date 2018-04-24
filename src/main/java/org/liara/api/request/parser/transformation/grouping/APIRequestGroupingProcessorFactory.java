package org.liara.api.request.parser.transformation.grouping;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

public final class APIRequestGroupingProcessorFactory
{
  public static <Entity> APIRequestGroupingProcessor<Entity> expression (
    @NonNull final String key, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return expression(key, (EntityFieldSelector<Entity, Expression<?>>) selector);
  }
  
  public static <Entity> APIRequestGroupingProcessor<Entity> expression (
    @NonNull final String key, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return new APIRequestExpressionGroupingProcessor<>(key, selector);
  }
  
  public static <Entity, Joined> APIRequestGroupingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return joinConfiguration(
      alias, (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, configuration
    );
  }
  
  public static <Entity, Joined> APIRequestGroupingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestJoinGroupingProcessor<>(
        alias, join, 
        new APIRequestConfigurationBasedGroupingProcessor<>(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestGroupingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> configuration
  ) {
    return joinCollection(
      alias, (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      configuration
    );
  }
  
  public static <Entity, Joined> APIRequestGroupingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> configuration
  ) {
    return new APIRequestJoinGroupingProcessor<>(
      alias, join, 
      new APIRequestConfigurationBasedGroupingProcessor<>(
          CollectionRequestConfiguration.getDefaultClass(configuration)
      )
    );
  }
}
