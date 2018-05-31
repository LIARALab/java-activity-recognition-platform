package org.liara.api.test.builder.state;

import org.liara.api.data.entity.state.BooleanStateCreationSchema;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaManager;
import org.springframework.lang.NonNull;

public class BooleanStateBuilder extends StateBuilder<BooleanStateBuilder>
{
  private boolean _value = false;
  
  public BooleanStateBuilder withValue (final boolean value) {
    _value = value;
    return self();
  }
  
  public void apply (@NonNull final BooleanStateCreationSchema schema) {
    super.apply(schema);
    schema.setValue(_value);
  }
  
  @Override
  public State build (@NonNull final SchemaManager manager) {
    final BooleanStateCreationSchema schema = new BooleanStateCreationSchema();
    apply(schema);
    
    return manager.execute(schema);
  }

  @Override
  public BooleanStateBuilder self () {
    return this;
  }
}
