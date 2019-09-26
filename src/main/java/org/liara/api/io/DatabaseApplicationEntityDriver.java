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
      info("io.handling " + creation.toString());
      _eventPublisher.initialize(creation.getEntities());
      info("io.handling.initialized " + creation.toString());
      _eventPublisher.willCreate(creation.getEntities());
      info("io.handling.pre-processed " + creation.toString());

      Arrays.stream(creation.getEntities()).forEach(_entityManager::persist);
      _entityManager.flush();
      info("io.handling.pushed " + creation.toString());

      _eventPublisher.didCreate(creation.getEntities());
      info("io.handling.post-processed " + creation.toString());
      info("io.handled " + creation.toString());
    } catch (@NonNull final Throwable throwable) {
      throw new Error("Error during creation event handling.", throwable);
    }
  }

  @EventListener
  public void update (@NonNull final UpdateApplicationEntityEvent mutation) {
    try {
      info("io.handling " + mutation.toString());
      _eventPublisher.willUpdate(mutation.getEntities());
      info("io.handling.pre-processed " + mutation.toString());

      Arrays.stream(mutation.getEntities()).forEach(_entityManager::merge);
      _entityManager.flush();
      info("io.handling.pushed " + mutation.toString());

      _eventPublisher.didUpdate(mutation.getEntities());
      info("io.handling.post-processed " + mutation.toString());
      info("io.handled " + mutation.toString());
    } catch (@NonNull final Throwable throwable) {
      throw new Error("Error during update event handling.", throwable);
    }
  }

  @EventListener
  public void delete (@NonNull final DeleteApplicationEntityEvent deletion) {
    try {
      info("io.handling " + deletion.toString());
      _eventPublisher.willDelete(deletion.getEntities());
      info("io.handling.pre-processed " + deletion.toString());

      Arrays.stream(deletion.getEntities()).forEach(_entityManager::remove);
      _entityManager.flush();
      info("io.handling.pushed " + deletion.toString());

      _eventPublisher.didDelete(deletion.getEntities());
      info("io.handling.post-processed " + deletion.toString());
      info("io.handled " + deletion.toString());
    } catch (@NonNull final Throwable throwable) {
      throw new Error("Error during deletion event handling.", throwable);
    }
  }

  @EventListener
  public void initialize (@NonNull final InitializeApplicationEntityEvent initialization) {
    for (@NonNull final ApplicationEntity applicationEntity : initialization.getEntities()) {
      applicationEntity.setCreationDate(ZonedDateTime.now());
      applicationEntity.setUpdateDate(ZonedDateTime.now());
      applicationEntity.setDeletionDate(null);
      if (applicationEntity.getUniversalUniqueIdentifier() == null)
        applicationEntity.setUniversalUniqueIdentifier(UUID.randomUUID().toString());
    }
  }
}
