package org.liara.api.data.entity.state;

import javax.persistence.EntityManager;

import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeMutatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

@SchemaHandler(StateMutationSchema.class)
public class StateMutationSchemaHandler
{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @NonNull
  private final StateCollection _states;
  
  @Autowired
  public StateMutationSchemaHandler (
    @NonNull final StateCollection states,
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
    _states = states;
  }
  
  public State handle (@NonNull final StateMutationSchema schema) {
    final EntityManager manager = _states.getManagerFactory().createEntityManager();
    _eventPublisher.publishEvent(new StateWillBeMutatedEvent(this, schema));
    final State state = schema.apply(_states);
    manager.merge(state);
    _eventPublisher.publishEvent(new StateWasMutatedEvent(this, state));
    manager.close();
    return state;
  }
}
