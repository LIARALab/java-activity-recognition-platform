package org.liara.api.metamodel.model;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface DeleteModelOperation<Entity>
  extends ModelController<Entity>
{
  /**
   * Delete a model of the application.
   *
   * @param identifier Identifier of the model to doDelete.
   *
   * @throws EntityNotFoundException If no model exists in the application with the given identifier.
   */
  void delete (@NonNull final Long identifier)
  throws EntityNotFoundException;

  /**
   * Delete a model of the application.
   *
   * @param identifier Identifier of the model to doDelete.
   *
   * @throws EntityNotFoundException If no model exists in the application with the given identifier.
   */
  void delete (@NonNull final UUID identifier)
  throws EntityNotFoundException;
}
