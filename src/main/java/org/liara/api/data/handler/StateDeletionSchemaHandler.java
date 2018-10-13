package org.liara.api.data.handler;

import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.data.schema.StateDeletionSchema;
import org.liara.api.event.StateWasDeletedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(StateDeletionSchema.class)
public class StateDeletionSchemaHandler
{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
    
  @Autowired
  public StateDeletionSchemaHandler (
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
  }
  
  public Object handle (
    @NonNull final EntityManager manager,
    @NonNull final StateDeletionSchema schema
  ) {
    _eventPublisher.publishEvent(new StateWillBeDeletedEvent(this, schema));
    final State toDelete = schema.getState().resolve(manager);
    final State snapshot = toDelete.clone();
    manager.remove(toDelete);
    _eventPublisher.publishEvent(new StateWasDeletedEvent(this, snapshot));
    return null;
  }
}
