package org.liara.api.data.entity.state;

import javax.persistence.EntityManager;

import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.event.StateWasDeletedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

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
    final State toDelete = schema.getState().resolve();
    final StateSnapshot snapshot = toDelete.snapshot();
    manager.remove(toDelete);
    _eventPublisher.publishEvent(new StateWasDeletedEvent(this, snapshot));
    return null;
  }
}
