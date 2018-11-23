package org.liara.api.data.entity.state.handler;

import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.LabelStateCreationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;

@SchemaHandler(LabelStateCreationSchema.class)
public class LabelStateCreationSchemaHandler
  extends BaseStateCreationSchemaHandler<LabelStateCreationSchema>
{
  @Autowired
  public LabelStateCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher publisher
  ) { super(publisher); }

  protected void apply (
    @NonNull final EntityManager manager,
    @NonNull final LabelStateCreationSchema schema,
    @NonNull final LabelState state
  ) {
    super.apply(manager, schema, state);
    
    state.setStart(schema.getStart());
    state.setEnd(schema.getEnd());
    state.setTag(schema.getTag());
  }

  @Override
  protected State instanciate (
    @NonNull final EntityManager manager, @NonNull final LabelStateCreationSchema schema
  ) {
    final LabelState result = new LabelState();
    apply(manager, schema, result);
    return result;
  }

  public State handle (
    @NonNull final EntityManager manager, @NonNull final LabelStateCreationSchema schema
  )
  {
    return super.handle(manager, schema);
  }
}
