package org.liara.api.event;

import org.liara.api.data.entity.state.StateSnapshot;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWasDeletedEvent extends ApplicationEvent
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
  public StateWasDeletedEvent(
    @NonNull final Object source,
    @NonNull final StateSnapshot state
  ) {
    super(source);
    _state = state;
  }

  public StateSnapshot getState () {
    return _state;
  }
}
