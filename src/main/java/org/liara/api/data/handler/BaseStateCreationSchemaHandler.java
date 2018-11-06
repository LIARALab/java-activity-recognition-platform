package org.liara.api.data.handler;

import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.StateCreationSchema;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.Map;

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
    state.setUpdateDate(ZonedDateTime.now());
    state.setDeletionDate(null);
    state.setEmissionDate(schema.getEmittionDate());
    state.setIdentifier(null);
    state.setSensor(schema.getSensor().resolve(manager));

    for (final Map.Entry<String, ApplicationEntityReference<State>> correlation : schema.getCorrelations().entrySet()) {
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

  protected State handle (
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
