package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.LabelStateCreationSchema;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

public class LocalLabelStateSchemaHandler
  extends BaseLocalStateCreationSchemaHandler<LabelStateCreationSchema, LabelState>
{
  @NonNull private final LocalEntityManager _manager;

  public LocalLabelStateSchemaHandler (
    @NonNull final LocalEntityManager manager
  ) { 
    super(manager); 
    _manager = manager;
  }

  @Override
  protected LabelState instanciate (@NonNull final LabelStateCreationSchema schema) {
    final LabelState state = new LabelState();
    apply(schema, state);
    state.setStart(schema.getStart());
    state.setEnd(schema.getEnd());
    state.setTag(schema.getTag());
    return state;
  }

}
