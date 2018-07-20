package org.liara.api.data.entity.state.handler;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.state.IntegerState;
import org.liara.api.data.entity.state.IntegerStateCreationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

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
  
  
}
