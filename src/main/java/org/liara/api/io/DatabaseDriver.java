package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.event.system.ApplicationResetEvent;
import org.liara.api.event.system.VirtualManagerResetEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseDriver
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final ApplicationEventPublisher _publisher;

  @Autowired
  public DatabaseDriver (
    @NonNull final EntityManager entityManager,
    @NonNull final ApplicationEventPublisher publisher
  ) {
    _entityManager = entityManager;
    _publisher = publisher;
  }

  @EventListener
  @Transactional
  public void reset (@NonNull final ApplicationResetEvent event) {
    _publisher.publishEvent(new VirtualManagerResetEvent(this));

    _entityManager.clear();
    _entityManager.createQuery("DELETE FROM " + State.class.getName()).executeUpdate();
    _entityManager.createQuery("DELETE FROM " + Sensor.class.getName()).executeUpdate();
    _entityManager.createQuery("DELETE FROM " + Node.class.getName()).executeUpdate();
  }
}
