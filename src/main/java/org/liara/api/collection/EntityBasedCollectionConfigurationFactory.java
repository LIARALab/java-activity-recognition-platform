package org.liara.api.collection;

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
public class EntityBasedCollectionConfigurationFactory
{
  @NonNull
  private final EntityBasedOrderingConfigurationFactory _orderingConfigurationFactory;

  @NonNull
  private final EntitySelectionConfigurationFactory _selectionConfigurationFactory;

  @Autowired
  public EntityBasedCollectionConfigurationFactory (
    @NonNull final EntityBasedOrderingConfigurationFactory orderingConfigurationFactory,
    @NonNull final EntitySelectionConfigurationFactory selectionConfigurationFactory
  )
  {
    _orderingConfigurationFactory = orderingConfigurationFactory;
    _selectionConfigurationFactory = selectionConfigurationFactory;
  }

  public <Entity> CollectionRequestConfiguration create (@NonNull final Class<Entity> type) {
    return new RequestConfigurationComposition(
      _orderingConfigurationFactory.getConfigurationOf(type),
      _selectionConfigurationFactory.getConfigurationOf(type),
      new SimpleRequestConfiguration(new APIRequestFreeCursorValidator(), new APIRequestFreeCursorParser())
    );
  }

  public <Entity> CollectionRequestConfiguration create (@NonNull final ManagedType<Entity> type) {
    return new RequestConfigurationComposition(
      _orderingConfigurationFactory.getConfigurationOf(type),
      _selectionConfigurationFactory.getConfigurationOf(type),
      new SimpleRequestConfiguration(new APIRequestFreeCursorValidator(), new APIRequestFreeCursorParser())
    );
  }
}
