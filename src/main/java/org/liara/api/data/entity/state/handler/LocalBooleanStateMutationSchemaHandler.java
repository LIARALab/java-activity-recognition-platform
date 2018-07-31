package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanStateMutationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

public class LocalBooleanStateMutationSchemaHandler
       extends LocalStateMutationSchemaHandler<BooleanStateMutationSchema>
{
  @NonNull
  private final LocalEntityManager _entityManager;
  
  public LocalBooleanStateMutationSchemaHandler(@NonNull final LocalEntityManager manager) {
    super(manager);
    _entityManager = manager;
  }

  public void apply (@NonNull final BooleanStateMutationSchema schema, @NonNull final BooleanState state) {
    super.apply(schema, state);
    if (schema.getValue() != null) state.setValue(schema.getValue());
  }
  
  public State resolve (@NonNull final BooleanStateMutationSchema input) {
    final BooleanState result = (BooleanState) _entityManager.find(input.getState()).get();
    _entityManager.remove(result);
    apply(input, result);
    return result;
  }
}
