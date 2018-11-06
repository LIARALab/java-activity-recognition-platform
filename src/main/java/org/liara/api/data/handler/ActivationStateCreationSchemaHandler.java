package org.liara.api.data.handler;

import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.ActivationStateCreationSchema;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(ActivationStateCreationSchema.class)
public class ActivationStateCreationSchemaHandler
       extends BaseStateCreationSchemaHandler<ActivationStateCreationSchema>
{
  @Autowired
  public ActivationStateCreationSchemaHandler(
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }

  protected void apply (
    @NonNull final EntityManager manager, 
    @NonNull final ActivationStateCreationSchema schema, 
    @NonNull final ActivationState state
  ) {
    super.apply(manager, schema, state);

    state.setStart(schema.getStart());
    state.setEnd(schema.getEnd());
    state.setNode(schema.getNode().resolve(manager));
  }

  @Override
  protected State instanciate (
    @NonNull final EntityManager manager, 
    @NonNull final ActivationStateCreationSchema schema
  ) {
    final ActivationState result = new ActivationState();
    apply(manager, schema, result);
    return result;
  }

  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final ActivationStateCreationSchema schema
  )
  {
    return super.handle(manager, schema);
  }
}