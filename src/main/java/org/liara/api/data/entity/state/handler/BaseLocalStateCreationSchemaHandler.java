package org.liara.api.data.entity.state.handler;

import java.time.ZonedDateTime;
import java.util.Map;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateCreationSchema;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

import com.google.common.base.Function;

public abstract class BaseLocalStateCreationSchemaHandler<Schema extends StateCreationSchema, Output extends State>
       implements Function<Schema, State>
{
  @NonNull
  private final LocalEntityManager _manager;
  
  public BaseLocalStateCreationSchemaHandler (
    @NonNull final LocalEntityManager manager
  ) {
    _manager = manager;
  }
  
  protected void apply (
    @NonNull final Schema schema, 
    @NonNull final State state
  ) {
    state.setCreationDate(ZonedDateTime.now());
    state.setDeletionDate(null);
    state.setUpdateDate(null);
    state.setEmittionDate(schema.getEmittionDate());
    state.setIdentifier(null);
    state.setSensor(_manager.find(schema.getSensor()).get());
    
    for (final Map.Entry<String, ApplicationEntityReference<State>> correlation : schema.correlations()) {
      state.correlate(correlation.getKey(), _manager.find(correlation.getValue()).get());
    }
  }
  
  protected abstract Output instanciate (@NonNull final Schema schema);
  
  @Override
  public State apply (@NonNull final Schema input) {
    final Output state = instanciate(input);
    _manager.add(state);
    return state;
  }
}
