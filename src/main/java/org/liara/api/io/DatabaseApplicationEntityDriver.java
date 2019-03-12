package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.entity.*;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseApplicationEntityDriver
  implements Loggable
{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseApplicationEntityDriver (
    @NonNull final ApplicationEventPublisher eventPublisher, @NonNull final EntityManager entityManager
  )
  {
    _eventPublisher = eventPublisher;
    _entityManager = entityManager;
  }

  @Transactional
  @EventListener
  public void create (final CreateApplicationEntityEvent creation) {
    _eventPublisher.publishEvent(new InitializeApplicationEntityEvent(
      this,
      creation.getEntities()
    ));
    _eventPublisher.publishEvent(new WillCreateApplicationEntityEvent(
      this,
      creation.getEntities()
    ));

    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      _entityManager.persist(entity);
      _entityManager.flush();
    }

    _eventPublisher.publishEvent(new DidCreateApplicationEntityEvent(this, creation.getEntities()));
  }

  @Transactional
  @EventListener
  public void update (final UpdateApplicationEntityEvent mutation) {
    _eventPublisher.publishEvent(new WillUpdateApplicationEntityEvent(
      this,
      mutation.getEntities()
    ));

    for (@NonNull final ApplicationEntity entity : mutation.getEntities()) {
      _entityManager.merge(entity);
      _entityManager.flush();
    }

    _eventPublisher.publishEvent(new DidUpdateApplicationEntityEvent(this, mutation.getEntities()));
  }

  @Transactional
  @EventListener
  public void delete (final DeleteApplicationEntityEvent deletion) {
    _eventPublisher.publishEvent(new WillDeleteApplicationEntityEvent(
      this,
      deletion.getEntities()
    ));

    for (@NonNull final ApplicationEntity entity : deletion.getEntities()) {
      _entityManager.remove(entity);
      _entityManager.flush();
    }

    _eventPublisher.publishEvent(new DidDeleteApplicationEntityEvent(this, deletion.getEntities()));
  }

  @EventListener
  public void initialize (final InitializeApplicationEntityEvent initialization) {
    for (@NonNull final ApplicationEntity applicationEntity : initialization.getEntities()) {
      applicationEntity.setCreationDate(ZonedDateTime.now());
      applicationEntity.setUpdateDate(ZonedDateTime.now());
      applicationEntity.setDeletionDate(null);
      if (applicationEntity.getUniversalUniqueIdentifier() == null)
        applicationEntity.setUniversalUniqueIdentifier(UUID.randomUUID().toString());
    }
  }
}
