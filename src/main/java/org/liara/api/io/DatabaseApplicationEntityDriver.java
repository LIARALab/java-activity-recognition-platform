package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.entity.CreateApplicationEntityEvent;
import org.liara.api.event.entity.DeleteApplicationEntityEvent;
import org.liara.api.event.entity.InitializeApplicationEntityEvent;
import org.liara.api.event.entity.UpdateApplicationEntityEvent;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseApplicationEntityDriver
  implements Loggable
{
  @NonNull
  private final APIEventPublisher _eventPublisher;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseApplicationEntityDriver (
    @NonNull final APIEventPublisher eventPublisher,
    @NonNull final EntityManager entityManager
  ) {
    _eventPublisher = eventPublisher;
    _entityManager = entityManager;
  }

  @EventListener
  public void create (@NonNull final CreateApplicationEntityEvent creation) {
    try {
      _eventPublisher.willConsume(creation);

      Arrays.stream(creation.getEntities()).forEach(
        (@NonNull final ApplicationEntity entity) -> {
          _eventPublisher.initialize(entity);
          _eventPublisher.willCreate(entity);
          _entityManager.persist(entity);
          _eventPublisher.didCreate(entity);
        }
      );
      _entityManager.flush();

      _eventPublisher.didConsume(creation);
    } catch (@NonNull final Throwable throwable) {
      _eventPublisher.didReject(creation);
      throw new Error("Error during creation event handling.", throwable);
    }
  }

  @EventListener
  public void update (@NonNull final UpdateApplicationEntityEvent mutation) {
    try {
      _eventPublisher.willConsume(mutation);

      Arrays.stream(mutation.getEntities()).forEach(
        (@NonNull final ApplicationEntity entity) -> {
          _eventPublisher.willUpdate(entity);
          _entityManager.merge(entity);
          _eventPublisher.didUpdate(entity);
        }
      );
      _entityManager.flush();

      _eventPublisher.didConsume(mutation);
    } catch (@NonNull final Throwable throwable) {
      _eventPublisher.didReject(mutation);
      throw new Error("Error during update event handling.", throwable);
    }
  }

  @EventListener
  public void delete (@NonNull final DeleteApplicationEntityEvent deletion) {
    try {
      _eventPublisher.willConsume(deletion);

      Arrays.stream(deletion.getEntities()).forEach(
        (@NonNull final ApplicationEntity entity) -> {
          _eventPublisher.willDelete(entity);
          _entityManager.createQuery(
            "DELETE FROM " + entity.getClass().getName() + " entity" +
            " WHERE entity.identifier = :identifier"
          ).setParameter("identifier", entity.getIdentifier()).executeUpdate();
          _eventPublisher.didDelete(entity);
        });
      _entityManager.flush();

      _eventPublisher.didConsume(deletion);
    } catch (@NonNull final Throwable throwable) {
      _eventPublisher.didReject(deletion);
      throw new Error("Error during deletion event handling.", throwable);
    }
  }

  @EventListener
  public void initialize (@NonNull final InitializeApplicationEntityEvent initialization) {
    _eventPublisher.willConsume(initialization);

    @NonNull final ApplicationEntity applicationEntity = initialization.getEntity();
    applicationEntity.setCreationDate(ZonedDateTime.now());
    applicationEntity.setUpdateDate(ZonedDateTime.now());
    applicationEntity.setDeletionDate(null);
    if (applicationEntity.getUniversalUniqueIdentifier() == null)
      applicationEntity.setUniversalUniqueIdentifier(UUID.randomUUID().toString());

    _eventPublisher.didConsume(initialization);
  }
}
