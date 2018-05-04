package org.liara.api.event;

import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateSnapshot;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWasCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 784473504749676317L;
  
  @NonNull
  private final StateSnapshot _state;
  
  /**
   * 
   * @param source
   * @param state
   */
  public StateWasCreatedEvent(
    @NonNull final Object source,
    @NonNull final State state
  ) {
    super(source);
    _state = state.snapshot();
  }

  public StateSnapshot getState () {
    return _state;
  }
}
