package org.liara.api.event;

import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateSnapshot;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWasMutatedEvent extends ApplicationEvent
{  
  /**
   * 
   */
  private static final long serialVersionUID = 8116556813252780522L;
  
  @NonNull
  private final StateSnapshot _state;
  
  /**
   * 
   * @param source
   * @param state
   */
  public StateWasMutatedEvent(
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
