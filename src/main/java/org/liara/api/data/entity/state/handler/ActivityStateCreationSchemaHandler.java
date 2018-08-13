package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.entity.state.ActivityStateCreationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(ActivityStateCreationSchema.class)
public class ActivityStateCreationSchemaHandler
       extends BaseStateCreationSchemaHandler<ActivityStateCreationSchema>
{
  @Autowired
  public ActivityStateCreationSchemaHandler(
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }

  protected void apply (
    @NonNull final EntityManager manager, 
    @NonNull final ActivityStateCreationSchema schema, 
    @NonNull final ActivityState state
  ) {
    super.apply(manager, schema, state);
    
    state.setStart(schema.getStart());
    state.setEnd(schema.getEnd());
    state.setTag(schema.getTag());
  }

  @Override
  protected State instanciate (
    @NonNull final EntityManager manager, 
    @NonNull final ActivityStateCreationSchema schema
  ) {
    final ActivityState result = new ActivityState();
    apply(manager, schema, result);
    return result;
  }

  public State handle (
    @NonNull final EntityManager manager,
    @NonNull final ActivityStateCreationSchema schema
  )
  {
    return super.handle(manager, schema);
  }
}
