package org.liara.api.data.entity.state;

import javax.persistence.EntityManager;

import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

@SchemaHandler(StateCreationSchema.class)
public class StateCreationSchemaHandler
{  
  @NonNull
  private final ApplicationEventPublisher _publisher;
  
  @Autowired
  public StateCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher publisher
  ) {
    _publisher = publisher;
  }
  
  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final StateCreationSchema schema
  ) {    
    _publisher.publishEvent(new StateWillBeCreatedEvent(this, schema));
    final State state = schema.create(manager);
    manager.persist(state);
    _publisher.publishEvent(new StateWasCreatedEvent(this, state));
    
    return state;
  }
}
