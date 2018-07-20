package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateCreationSchema;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

public class LocalStateCreationSchemaHandler
       extends BaseLocalStateCreationSchemaHandler<StateCreationSchema, State>
{
  public LocalStateCreationSchemaHandler(
    @NonNull final LocalEntityManager manager
  ) { super(manager); }

  @Override
  protected State instanciate (@NonNull final StateCreationSchema schema) {
    final State state = new State();
    apply(schema, state);
    return state;
  }
}
