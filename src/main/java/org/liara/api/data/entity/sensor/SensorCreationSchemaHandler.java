package org.liara.api.data.entity.sensor;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.event.SensorWasCreatedEvent;
import org.liara.api.event.SensorWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

@SchemaHandler(SensorCreationSchema.class)
public class SensorCreationSchemaHandler
{  
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @Autowired
  public SensorCreationSchemaHandler (
    @NonNull final NodeCollection nodes,
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
  }
  
  @Transactional
  public Sensor handle (
    @NonNull final EntityManager manager,
    @NonNull final SensorCreationSchema schema
  ) {
    _eventPublisher.publishEvent(new SensorWillBeCreatedEvent(this, schema));
    final Sensor sensor = new Sensor(schema);
    manager.persist(sensor);
    _eventPublisher.publishEvent(new SensorWasCreatedEvent(this, sensor));
    
    return sensor;
  }
}
