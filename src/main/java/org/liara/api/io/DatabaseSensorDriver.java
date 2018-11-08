package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.SensorEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseSensorDriver
{
  @NonNull
  private final ApplicationEventPublisher _publisher;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseSensorDriver (
    @NonNull final ApplicationEventPublisher publisher, @NonNull final EntityManager entityManager
  )
  {
    _publisher = publisher;
    _entityManager = entityManager;
  }

  @EventListener
  public void create (final ApplicationEntityEvent.@NonNull Create creation) {
    if (creation.getApplicationEntity() instanceof Sensor) {
      @NonNull final Sensor sensor = (Sensor) creation.getApplicationEntity();

      _publisher.publishEvent(new SensorEvent.WillBeCreated(this, sensor));
      _entityManager.persist(sensor);
      _publisher.publishEvent(new SensorEvent.WasCreated(this, sensor));
    }
  }
}
