package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.ActivationStateMutationSchema;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

public class LocalActivationStateMutationSchemaHandler
       extends LocalStateMutationSchemaHandler<ActivationStateMutationSchema>
{
  @NonNull
  private final LocalEntityManager _manager;
  
  public LocalActivationStateMutationSchemaHandler (
    @NonNull final LocalEntityManager manager
  ) {
    super(manager);
    _manager = manager;
  }

  public void apply (@NonNull final ActivationStateMutationSchema schema, @NonNull final ActivationState state) {
    super.apply(schema, state);
    if (schema.getStart() != null) state.setStart(schema.getStart());
    if (schema.getEnd() != null) state.setEnd(schema.getEnd());
    if (!schema.getNode().isNull()) state.setNode(_manager.find(schema.getNode()).get());
  }
  
  public State resolve (@NonNull final ActivationStateMutationSchema input) {
    final ActivationState result = (ActivationState) _manager.find(input.getState()).get();
    _manager.remove(result);
    apply(input, result);
    return result;
  }
}
