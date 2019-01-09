package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.logging.Loggable;
import org.liara.api.utils.ObjectIdentifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CollectionControllerManagerConfigurator
  implements Loggable
{
  @NonNull
  private final ApplicationContext _applicationContext;

  @NonNull
  private final CollectionControllerManager _collectionControllerManager;

  @Autowired
  public CollectionControllerManagerConfigurator (
    @NonNull final ApplicationContext applicationContext,
    @NonNull final CollectionControllerManager collectionControllerManager
  ) {
    _collectionControllerManager = collectionControllerManager;
    _applicationContext = applicationContext;
  }

  @PostConstruct
  public void captureStartupApplicationCollectionControllers () {
    @NonNull final ApplicationEventPublisher publisher = _applicationContext.getBean(ApplicationEventPublisher.class);
    @NonNull final Collection<Object> controllers = _applicationContext.getBeansWithAnnotation(
      CollectionController.RegisterOnStartup.class
    ).values();

    for (@NonNull final Object controller : controllers) {
      if (controller instanceof CollectionController) {
        publisher.publishEvent(
          new CollectionControllerAdditionEvent(this, (CollectionController) controller)
        );
      } else {
        throw new Error(
          "Object " + ObjectIdentifiers.getIdentifier(controller) + " annotated with " +
          CollectionController.RegisterOnStartup.class.toString() + " without being an instance of " +
          CollectionController.class.toString() + "."
        );
      }
    }
  }

  @EventListener
  public void handleCollectionControllerAddition (@NonNull final CollectionControllerAdditionEvent addition) {
    _collectionControllerManager.put(
      addition.getName(),
      addition.getCollectionController()
    );

    info(
      "collectionControllers.added " + ObjectIdentifiers.getIdentifier(addition.getCollectionController()) +
      " as " + addition.getName()
    );
  }

  @EventListener
  public void handleCollectionControllerDeletion (@NonNull final CollectionControllerDeletionEvent deletion) {
    if (_collectionControllerManager.contains(deletion.getName())) {
      @NonNull final CollectionController<?> removed = _collectionControllerManager.get(deletion.getName());

      _collectionControllerManager.remove(deletion.getName());

      info(
        "collectionControllers.removed " + ObjectIdentifiers.getIdentifier(removed) + " as " + deletion.getName()
      );
    }
  }
}
