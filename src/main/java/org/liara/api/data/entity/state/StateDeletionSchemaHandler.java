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
  
  @NonNull
  private final EntityManager _manager;
  
  @Autowired
  public StateDeletionSchemaHandler (
    @NonNull final EntityManager manager,
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
    _manager = manager;
  }
  
  public Object handle (@NonNull final StateDeletionSchema schema) {
    _eventPublisher.publishEvent(new StateWillBeDeletedEvent(this, schema));
    final State toDelete = _manager.find(State.class, schema.getIdentifier());
    final StateSnapshot snapshot = toDelete.snapshot();
    _manager.remove(toDelete);
    _eventPublisher.publishEvent(new StateWasDeletedEvent(this, snapshot));
    return null;
  }
}
