package org.liara.api.data.handler;

import org.liara.api.data.entity.state.IntegerState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.IntegerStateCreationSchema;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(IntegerStateCreationSchema.class)
public class IntegerStateCreationSchemaHandler
       extends BaseStateCreationSchemaHandler<IntegerStateCreationSchema>
{
  @Autowired
  public IntegerStateCreationSchemaHandler(
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }

  protected void apply (
    @NonNull final EntityManager manager, 
    @NonNull final IntegerStateCreationSchema schema, 
    @NonNull final IntegerState state
  ) {
    super.apply(manager, schema, state);
    state.setValue(schema.getValue());  
  }

  @Override
  protected State instanciate (
    @NonNull final EntityManager manager, 
    @NonNull final IntegerStateCreationSchema schema
  ) {
    final IntegerState result = new IntegerState();
    apply(manager, schema, result);
    return result;
  }

  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final IntegerStateCreationSchema schema
  )
  {
    return super.handle(manager, schema);
  }
}
