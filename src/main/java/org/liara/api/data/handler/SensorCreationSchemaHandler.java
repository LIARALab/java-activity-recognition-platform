package org.liara.api.data.handler;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.data.schema.SensorCreationSchema;
import org.liara.api.event.SensorEvent;
import org.liara.api.event.SensorWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;

@SchemaHandler(SensorCreationSchema.class)
public class SensorCreationSchemaHandler
{  
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @Autowired
  public SensorCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
  }
  
  @Transactional
  public @NonNull
  Sensor handle (
    @NonNull final EntityManager entityManager, @NonNull final SensorCreationSchema creationSchema
  ) {
    _eventPublisher.publishEvent(new SensorWillBeCreatedEvent(this, creationSchema));
    final Sensor sensor = new Sensor();
    sensor.setCreationDate(ZonedDateTime.now());
    sensor.setUpdateDate(ZonedDateTime.now());
    sensor.setDeletionDate(null);
    sensor.setUUID(creationSchema.getUUID() == null ? null : UUID.randomUUID());
    sensor.setName(creationSchema.getName());
    sensor.setConfiguration(creationSchema.getConfiguration());
    sensor.setType(creationSchema.getType());
    sensor.setNode(creationSchema.getParent().resolve(entityManager));
    sensor.setUnit(creationSchema.getUnit());
    entityManager.persist(sensor);
    _eventPublisher.publishEvent(new SensorEvent(this, sensor));
    
    return sensor;
  }
}
