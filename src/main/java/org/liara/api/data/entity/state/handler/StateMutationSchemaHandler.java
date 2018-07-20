package org.liara.api.data.entity.state.handler;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateMutationSchema;
import org.liara.api.data.entity.state.StateSnapshot;
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
  
  @Autowired
  public StateMutationSchemaHandler (
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
  }
  
  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final StateMutationSchema schema
  ) {
    _eventPublisher.publishEvent(new StateWillBeMutatedEvent(this, schema));
    final StateSnapshot oldValue = schema.getState().resolve(manager).snapshot();
    final State state = schema.apply();
    manager.merge(state);
    _eventPublisher.publishEvent(new StateWasMutatedEvent(this, oldValue, state));
    return state;
  }
}
