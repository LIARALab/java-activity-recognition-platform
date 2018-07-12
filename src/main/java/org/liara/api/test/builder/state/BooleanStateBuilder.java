package org.liara.api.test.builder.state;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

public class BooleanStateBuilder extends StateBuilder<BooleanStateBuilder>
{
  public static BooleanStateBuilder truthy () {
    return new BooleanStateBuilder().withValue(true);
  }
  
  public static BooleanStateBuilder falsy () {
    return new BooleanStateBuilder().withValue(false);
  }
  
  private boolean _value = false;
  
  public BooleanStateBuilder withValue (final boolean value) {
    _value = value;
    return self();
  }
  
  protected void apply (@NonNull final BooleanState state) {
    super.apply(state);
    state.setValue(_value);
  }
  
  @Override
  public State build () {
    final BooleanState state = new BooleanState();
    
    apply(state);
    
    return state;
  }

  @Override
  public BooleanStateBuilder self () {
    return this;
  }
}
