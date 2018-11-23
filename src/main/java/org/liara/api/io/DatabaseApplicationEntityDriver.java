package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.ApplicationEntityEvent;
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
  public void create (final ApplicationEntityEvent.@NonNull Create creation) {
    _eventPublisher.publishEvent(new ApplicationEntityEvent.Initialize(this, creation.getEntities()));
    _eventPublisher.publishEvent(new ApplicationEntityEvent.WillCreate(this, creation.getEntities()));

    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      _entityManager.persist(entity);
    }

    _eventPublisher.publishEvent(new ApplicationEntityEvent.DidCreate(this, creation.getEntities()));
  }

  @Transactional
  @EventListener
  public void update (final ApplicationEntityEvent.@NonNull Update mutation) {
    _eventPublisher.publishEvent(new ApplicationEntityEvent.WillUpdate(this, mutation.getEntities()));

    for (@NonNull final ApplicationEntity entity : mutation.getEntities()) {
      _entityManager.persist(entity);
    }

    _eventPublisher.publishEvent(new ApplicationEntityEvent.DidUpdate(this, mutation.getEntities()));
  }

  @Transactional
  @EventListener
  public void delete (final ApplicationEntityEvent.@NonNull Delete deletion) {
    _eventPublisher.publishEvent(new ApplicationEntityEvent.WillDelete(this, deletion.getEntities()));

    for (@NonNull final ApplicationEntity entity : deletion.getEntities()) {
      _entityManager.remove(entity);
    }

    _eventPublisher.publishEvent(new ApplicationEntityEvent.DidDelete(this, deletion.getEntities()));
  }

  @EventListener
  public void initialize (final ApplicationEntityEvent.@NonNull Initialize initialization) {
    for (@NonNull final ApplicationEntity applicationEntity : initialization.getEntities()) {
      applicationEntity.setCreationDate(ZonedDateTime.now());
      applicationEntity.setUpdateDate(ZonedDateTime.now());
      applicationEntity.setDeletionDate(null);
      if (applicationEntity.getUUID() == null) applicationEntity.setUUID(UUID.randomUUID());
    }
  }
}
