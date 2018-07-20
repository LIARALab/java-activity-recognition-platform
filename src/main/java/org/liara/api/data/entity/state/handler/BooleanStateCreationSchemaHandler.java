package org.liara.api.data.entity.state.handler;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanStateCreationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

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
  
  
}
