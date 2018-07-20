package org.liara.api.data.entity.state.handler;

import java.time.ZonedDateTime;
import java.util.Map;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateCreationSchema;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

public class BaseStateCreationSchemaHandler<Schema extends StateCreationSchema>
{  
  @NonNull
  private final ApplicationEventPublisher _publisher;
  
  @Autowired
  public BaseStateCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher publisher
  ) {
    _publisher = publisher;
  }
  
  protected void apply (
    @NonNull final EntityManager manager,
    @NonNull final Schema schema, 
    @NonNull final State state
  ) {
    state.setCreationDate(ZonedDateTime.now());
    state.setDeletionDate(null);
    state.setUpdateDate(null);
    state.setEmittionDate(schema.getEmittionDate());
    state.setIdentifier(null);
    state.setSensor(schema.getSensor().resolve(manager));
    
    for (final Map.Entry<String, ApplicationEntityReference<State>> correlation : schema.correlations()) {
      state.correlate(correlation.getKey(), correlation.getValue().resolve(manager));
    }
  }
  
  protected State instanciate (
    @NonNull final EntityManager manager,
    @NonNull final Schema schema
  ) {
    final State state = new State();
    apply(manager, schema, state);
    return state;
  }
  
  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final Schema schema
  ) {    
    _publisher.publishEvent(new StateWillBeCreatedEvent(this, schema));
    final State state = instanciate(manager, schema);
    manager.persist(state);
    _publisher.publishEvent(new StateWasCreatedEvent(this, state));
    
    return state;
  }
}
