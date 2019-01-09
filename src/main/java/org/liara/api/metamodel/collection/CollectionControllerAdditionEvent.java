package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationEvent;

public class CollectionControllerAdditionEvent
  extends ApplicationEvent
{
  @NonNull
  private final String _name;

  @NonNull
  private final CollectionController<?> _collectionController;

  public CollectionControllerAdditionEvent (
    @NonNull final Object source,
    @NonNull final CollectionController<?> collectionController
  ) {
    super(source);
    _name = CollectionController.getNameOf(collectionController);
    _collectionController = collectionController;
  }

  public CollectionControllerAdditionEvent (
    @NonNull final Object source,
    @NonNull final String name,
    @NonNull final CollectionController<?> collectionController
  ) {
    super(source);
    _name = name;
    _collectionController = collectionController;
  }

  public CollectionControllerAdditionEvent (
    @NonNull final CollectionControllerAdditionEvent toCopy
  ) {
    super(toCopy.getSource());
    _name = toCopy.getName();
    _collectionController = toCopy.getCollectionController();
  }

  public @NonNull String getName () {
    return _name;
  }

  public @NonNull CollectionController<?> getCollectionController () {
    return _collectionController;
  }
}
