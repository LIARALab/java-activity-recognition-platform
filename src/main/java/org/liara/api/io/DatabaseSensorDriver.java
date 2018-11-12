package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.SensorEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseSensorDriver
{
  @NonNull
  private final ApplicationEventPublisher _publisher;

  @Autowired
  public DatabaseSensorDriver (
    @NonNull final ApplicationEventPublisher publisher
  )
  {
    _publisher = publisher;
  }

  @EventListener
  public void willCreate (final ApplicationEntityEvent.@NonNull WillCreate creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof Sensor) _publisher.publishEvent(new SensorEvent.WillBeCreated(this, (Sensor) entity));
    }
  }

  @EventListener
  public void didCreate (final ApplicationEntityEvent.@NonNull DidCreate creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof Sensor) _publisher.publishEvent(new SensorEvent.WasCreated(this, (Sensor) entity));
    }
  }
}
