package org.liara.api.metamodel.model;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface GetModelOperation<Model>
  extends ModelController<Model>
{
  /**
   * Get a model of the application.
   *
   * @param identifier Identifier of the model to get.
   *
   * @return The requested model.
   *
   * @throws EntityNotFoundException If no model exists in the application with the given identifier.
   */
  @NonNull Model get (@NonNull final Long identifier)
  throws EntityNotFoundException;

  /**
   * Get a model of the application.
   *
   * @param identifier Identifier of the model to get.
   *
   * @return The requested model.
   *
   * @throws EntityNotFoundException If no model exists in the application with the given identifier.
   */
  @NonNull Model get (@NonNull final UUID identifier)
  throws EntityNotFoundException;
}
