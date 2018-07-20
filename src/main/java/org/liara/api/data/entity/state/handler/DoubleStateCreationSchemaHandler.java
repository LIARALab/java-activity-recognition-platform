package org.liara.api.data.entity.state.handler;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.state.DoubleState;
import org.liara.api.data.entity.state.DoubleStateCreationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

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
  
  
}
