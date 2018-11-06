package org.liara.api.data.handler;

import org.liara.api.data.entity.state.DoubleState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.DoubleStateCreationSchema;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(DoubleStateCreationSchema.class)
public class DoubleStateCreationSchemaHandler
       extends BaseStateCreationSchemaHandler<DoubleStateCreationSchema>
{
  @Autowired
  public DoubleStateCreationSchemaHandler(
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }

  protected void apply (
    @NonNull final EntityManager manager, 
    @NonNull final DoubleStateCreationSchema schema, 
    @NonNull final DoubleState state
  ) {
    super.apply(manager, schema, state);
    state.setValue(schema.getValue());  
  }

  @Override
  protected State instanciate (
    @NonNull final EntityManager manager, 
    @NonNull final DoubleStateCreationSchema schema
  ) {
    final DoubleState result = new DoubleState();
    apply(manager, schema, result);
    return result;
  }

  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final DoubleStateCreationSchema schema
  )
  {
    return super.handle(manager, schema);
  }
}