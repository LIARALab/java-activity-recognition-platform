package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface DeletableCollectionController<Entity> extends CollectionController<Entity>
{
  void delete (@NonNull final Long identifier)
  throws EntityNotFoundException;

  void delete (@NonNull final UUID identifier)
  throws EntityNotFoundException;
}
