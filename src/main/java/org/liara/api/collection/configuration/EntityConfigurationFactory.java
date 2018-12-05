package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.cursor.APIRequestFreeCursorParser;
import org.liara.api.request.cursor.APIRequestFreeCursorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.ManagedType;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntityConfigurationFactory
{
  @NonNull
  private final EntityOrderingConfigurationFactory _orderingConfigurationFactory;

  @NonNull
  private final EntityFilteringConfigurationFactory _selectionConfigurationFactory;

  @Autowired
  public EntityConfigurationFactory (
    @NonNull final EntityOrderingConfigurationFactory orderingConfigurationFactory,
    @NonNull final EntityFilteringConfigurationFactory selectionConfigurationFactory
  )
  {
    _orderingConfigurationFactory = orderingConfigurationFactory;
    _selectionConfigurationFactory = selectionConfigurationFactory;
  }

  public <Entity> RequestConfiguration create (@NonNull final Class<Entity> type) {
    return new RequestConfigurationComposition(RequestConfiguration.field(
      "orderby",
      _orderingConfigurationFactory.getConfigurationOf(type)
    ),
      _selectionConfigurationFactory.getConfigurationOf(type),
      new SimpleRequestConfiguration(new APIRequestFreeCursorParser(), new APIRequestFreeCursorValidator())
    );
  }

  public <Entity> RequestConfiguration create (@NonNull final ManagedType<Entity> type) {
    return new RequestConfigurationComposition(RequestConfiguration.field(
      "orderby",
      _orderingConfigurationFactory.getConfigurationOf(type)
    ),
      _selectionConfigurationFactory.getConfigurationOf(type),
      new SimpleRequestConfiguration(new APIRequestFreeCursorParser(), new APIRequestFreeCursorValidator())
    );
  }
}
