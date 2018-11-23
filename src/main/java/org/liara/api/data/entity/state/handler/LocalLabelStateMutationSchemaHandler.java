package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.LabelStateMutationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

public class LocalLabelStateMutationSchemaHandler
  extends LocalStateMutationSchemaHandler<LabelStateMutationSchema>
{
  @NonNull
  private final LocalEntityManager _manager;

  public LocalLabelStateMutationSchemaHandler (
    @NonNull final LocalEntityManager manager
  ) {
    super(manager);
    _manager = manager;
  }

  public void apply (@NonNull final LabelStateMutationSchema schema, @NonNull final LabelState state) {
    super.apply(schema, state);
    if (schema.getStart() != null) state.setStart(schema.getStart());
    if (schema.getEnd() != null) state.setEnd(schema.getEnd());
    if (schema.getTag() != null) state.setTag(schema.getTag());
  }

  public State resolve (@NonNull final LabelStateMutationSchema input) {
    final LabelState result = (LabelState) _manager.find(input.getState()).get();
    _manager.remove(result);
    apply(input, result);
    return result;
  }
}
