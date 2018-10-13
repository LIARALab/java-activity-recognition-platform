package org.liara.api.data.handler;

import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.data.schema.ActivationStateCreationSchema;
import org.springframework.lang.NonNull;

public class LocalActivationStateSchemaHandler
       extends BaseLocalStateCreationSchemaHandler<ActivationStateCreationSchema, ActivationState>
{
  @NonNull private final LocalEntityManager _manager;
  
  public LocalActivationStateSchemaHandler(
    @NonNull final LocalEntityManager manager
  ) { 
    super(manager); 
    _manager = manager;
  }

  @Override
  protected ActivationState instanciate (@NonNull final ActivationStateCreationSchema schema) {
    final ActivationState state = new ActivationState();
    apply(schema, state);
    state.setStart(schema.getStart());
    state.setEnd(schema.getEnd());
    state.setNode(_manager.find(schema.getNode()).get());
    return state;
  }

}
