package org.liara.api.data.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;

public class ApplicationEntityCreationSchemaHandler<CreationSchema extends ApplicationEntityCreationSchema>
{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;

  @Autowired
  public ApplicationEntityCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher eventPublisher
  )
  {
    _eventPublisher = eventPublisher;
  }

  public ApplicationEntity create (
    @NonNull final EntityManager manager,
    @NonNull final CreationSchema schema
  )
  {
    return new ApplicationEntity();
  }

  public ApplicationEntity handle (
    @NonNull final EntityManager manager,
    @NonNull final CreationSchema schema
  )
  {
    final ApplicationEntity entity = create(manager, schema);
    entity.setCreationDate(ZonedDateTime.now());
    entity.setUpdateDate(ZonedDateTime.now());
    entity.setDeletionDate(null);
    manager.persist(entity);

    return entity;
  }

  public ApplicationEventPublisher getEventPublisher () {
    return _eventPublisher;
  }
}
