package org.liara.api.request.parser.operator.ordering;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

public final class APIRequestOrderingProcessorFactory
{
  public static <Entity> APIRequestOrderingProcessor<Entity> field (
    @NonNull final String key, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return field(key, (EntityFieldSelector<Entity, Expression<?>>) selector);
  }
  
  public static <Entity> APIRequestOrderingProcessor<Entity> field (
    @NonNull final String key, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return new APIRequestFieldOrderingProcessor<>(key, selector);
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return joinConfiguration(
      alias, 
      (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      configuration
    );
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestJoinOrderingProcessor<>(
      alias, join, 
      new APIRequestConfigurationBasedOrderingProcessor<>(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> configuration
  ) {
    return joinConfiguration(
      alias, 
      (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> configuration
  ) {
    return joinConfiguration(
      alias, 
      join, 
      CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
}
