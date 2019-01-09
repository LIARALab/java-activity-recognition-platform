package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.model.GetModelOperation;
import org.liara.api.metamodel.model.ModelController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

public class ApplicationModelController<Model>
  implements ModelController<Model>,
             GetModelOperation<Model>
{
  @NonNull
  private final Class<Model> _modelClass;

  @NonNull
  private final ReadableControllerConfiguration _configuration;

  protected ApplicationModelController (
    @NonNull final Class<Model> modelClass, @NonNull final ReadableControllerConfiguration configuration
  )
  {
    _modelClass = modelClass;
    _configuration = configuration;
  }

  @Override
  public @NonNull Class<Model> getModelClass () {
    return _modelClass;
  }

  @NonNull
  @Override
  public Model get (@NonNull final Long identifier)
  throws EntityNotFoundException
  {
    return _configuration.getEntityManager().find(_modelClass, identifier);
  }

  @NonNull
  @Override
  public Model get (@NonNull final UUID identifier)
  throws EntityNotFoundException
  {
    @NonNull final List<Model> result = _configuration.getEntityManager().createQuery(
      "SELECT entity FROM " + getModelClass().getName() + " entity WHERE entity.universalUniqueIdentifier = :uuid",
      getModelClass()
    ).setParameter("uuid", identifier.toString()).getResultList();

    if (result.size() > 0) {
      return result.get(0);
    } else {
      throw new EntityNotFoundException();
    }
  }
}
