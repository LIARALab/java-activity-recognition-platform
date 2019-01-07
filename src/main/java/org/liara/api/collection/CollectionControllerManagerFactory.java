package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.logging.InstantiationMessageFactory;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionControllerManagerFactory
  implements Loggable
{
  @NonNull
  private final ApplicationContext _context;

  @Nullable
  private CollectionControllerManager _instance;

  @Autowired
  public CollectionControllerManagerFactory (@NonNull final ApplicationContext context) {
    _context = context;
  }

  @Bean("collectionControllerManager")
  public @NonNull CollectionControllerManager get () {
    if (_instance == null) instantiate();

    return _instance;
  }

  private void instantiate () {
    info(InstantiationMessageFactory.instantiating("collectionControllerManager"));

    @NonNull final CollectionControllerManager manager = new CollectionControllerManager();
    @NonNull final Map<@NonNull String, @NonNull Object> controllers = _context.getBeansWithAnnotation(
      CollectionController.class);

    for (@NonNull final Object controller : controllers.values()) {
      manager.register(controller);
    }

    _instance = manager;

    info(InstantiationMessageFactory.instantiated("collectionControllerManager", manager));
  }
}
