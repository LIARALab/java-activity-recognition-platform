package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.StateCreationSchema;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

@SchemaHandler(StateCreationSchema.class)
public class StateCreationSchemaHandler
       extends BaseStateCreationSchemaHandler<StateCreationSchema>
{
  @Autowired
  public StateCreationSchemaHandler(
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }
}
