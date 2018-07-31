package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanStateCreationSchema;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

public class LocalBooleanStateCreationSchemaHandler
       extends BaseLocalStateCreationSchemaHandler<BooleanStateCreationSchema, BooleanState>
{
  public LocalBooleanStateCreationSchemaHandler(LocalEntityManager manager) {
    super(manager);
  }

  @Override
  protected BooleanState instanciate (@NonNull final BooleanStateCreationSchema schema) {
    final BooleanState state = new BooleanState();
    apply(schema, state);
    state.setValue(schema.getValue());
    return state;
  }
}
