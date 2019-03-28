package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.entity.CreateApplicationEntityEvent;
import org.liara.api.event.entity.DeleteApplicationEntityEvent;
import org.liara.api.event.entity.InitializeApplicationEntityEvent;
import org.liara.api.event.entity.UpdateApplicationEntityEvent;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseApplicationEntityDriver
  implements Loggable
{
  @NonNull
  private final APIEventPublisher _eventPublisher;

  @NonNull
  private final EntityManager _entityManager;

  @Nullable
  private Object _transactionalEvent = null;

  @Autowired
  public DatabaseApplicationEntityDriver (
    @NonNull final APIEventPublisher eventPublisher,
    @NonNull @Qualifier("generatorEntityManager") final EntityManager entityManager
  ) {
    _eventPublisher = eventPublisher;
    _entityManager = entityManager;
  }

  @EventListener
  public void create (@NonNull final CreateApplicationEntityEvent creation) {
    beginTransactionFor(creation);

    try {
      _eventPublisher.initialize(creation.getEntities());
      _eventPublisher.willCreate(creation.getEntities());

      Arrays.stream(creation.getEntities()).forEach(_entityManager::persist);
      _entityManager.flush();

      _eventPublisher.didCreate(creation.getEntities());
      endTransactionOf(creation);
    } catch (@NonNull final Throwable throwable) {
      rollback(creation, throwable);
      throw new Error("Error during creation event handling.", throwable);
    }
  }

  @EventListener
  public void update (@NonNull final UpdateApplicationEntityEvent mutation) {
    beginTransactionFor(mutation);

    try {
      _eventPublisher.willUpdate(mutation.getEntities());

      Arrays.stream(mutation.getEntities()).forEach(_entityManager::merge);
      _entityManager.flush();

      _eventPublisher.didUpdate(mutation.getEntities());
      endTransactionOf(mutation);
    } catch (@NonNull final Throwable throwable) {
      rollback(mutation, throwable);
      throw new Error("Error during update event handling.", throwable);
    }
  }

  @EventListener
  public void delete (@NonNull final DeleteApplicationEntityEvent deletion) {
    beginTransactionFor(deletion);

    try {
      _eventPublisher.willDelete(deletion.getEntities());

      Arrays.stream(deletion.getEntities()).forEach(_entityManager::remove);
      _entityManager.flush();

      _eventPublisher.didDelete(deletion.getEntities());
      endTransactionOf(deletion);
    } catch (@NonNull final Throwable throwable) {
      rollback(deletion, throwable);
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

  private void beginTransactionFor (@NonNull final Object event) {
    if (_transactionalEvent == null) {
      _transactionalEvent = event;
      _entityManager.getTransaction().begin();
    }
  }

  private void endTransactionOf (@NonNull final Object event) {
    if (_transactionalEvent == event) {
      _transactionalEvent = null;
      _entityManager.getTransaction().commit();
    }
  }

  private void rollback (@NonNull final Object event, @NonNull final Throwable throwable) {
    if (_transactionalEvent == event) {
      _transactionalEvent = null;
      _entityManager.getTransaction().rollback();

      getLogger().throwing(getClass().getName(), "rollback", throwable);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) {
        throwable.printStackTrace();
      }
    }
  }
}
