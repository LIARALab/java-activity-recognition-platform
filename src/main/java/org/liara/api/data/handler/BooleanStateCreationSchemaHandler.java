package org.liara.api.data.handler;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.BooleanStateCreationSchema;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(BooleanStateCreationSchema.class)
public class BooleanStateCreationSchemaHandler
       extends BaseStateCreationSchemaHandler<BooleanStateCreationSchema>
{
  @Autowired
  public BooleanStateCreationSchemaHandler(
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }

  protected void apply (
    @NonNull final EntityManager manager, 
    @NonNull final BooleanStateCreationSchema schema, 
    @NonNull final BooleanState state
  ) {
    super.apply(manager, schema, state);
    state.setValue(schema.getValue());  
  }

  @Override
  protected State instanciate (
    @NonNull final EntityManager manager, 
    @NonNull final BooleanStateCreationSchema schema
  ) {
    final BooleanState result = new BooleanState();
    apply(manager, schema, result);
    return result;
  }

  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final BooleanStateCreationSchema schema
  )
  {
    return super.handle(manager, schema);
  }
}
