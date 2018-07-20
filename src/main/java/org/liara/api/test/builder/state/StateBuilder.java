package org.liara.api.test.builder.state;

import org.liara.api.data.entity.state.State;

public class StateBuilder extends BaseStateBuilder<StateBuilder, State>
{
  @Override
  public State build () {
    final State result = new State();
    apply(result);
    return result;
  }

  @Override
  public StateBuilder self () {
    return this;
  }
}
