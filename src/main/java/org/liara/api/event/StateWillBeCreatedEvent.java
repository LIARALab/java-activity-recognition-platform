package org.liara.api.event;

import org.liara.api.data.entity.state.StateCreationSchema;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWillBeCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = -5512592927163401545L;

  @NonNull
  private final StateCreationSchema _state;
  
  public StateWillBeCreatedEvent(
    @NonNull final Object source,
    @NonNull final StateCreationSchema state
  ) {
    super(source);
    _state = state;
  }
  
  public StateCreationSchema getState () {
    return _state;
  }
}
