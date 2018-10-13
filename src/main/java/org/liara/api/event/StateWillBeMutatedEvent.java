package org.liara.api.event;

import org.liara.api.data.schema.StateMutationSchema;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWillBeMutatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 7136690898888197015L;
  
  @NonNull
  private final StateMutationSchema _state;
  
  public StateWillBeMutatedEvent(
    @NonNull final Object source,
    @NonNull final StateMutationSchema state
  ) {
    super(source);
    _state = state;
  }
  
  public StateMutationSchema getState () {
    return _state;
  }
}
