package org.liara.api.event;

import org.liara.api.data.entity.state.State;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWasDeletedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 784473504749676317L;
  
  @NonNull
  private final State _state;
  
  /**
   * 
   * @param source
   * @param state
   */
  public StateWasDeletedEvent(
    @NonNull final Object source, @NonNull final State state
  ) {
    super(source);
    _state = state;
  }

  public State getState () {
    return _state;
  }
}
