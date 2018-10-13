package org.liara.api.event;

import org.liara.api.data.schema.StateDeletionSchema;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWillBeDeletedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = -1124145538450724820L;
  
  @NonNull
  private final StateDeletionSchema _state;
  
  public StateWillBeDeletedEvent(
    @NonNull final Object source,
    @NonNull final StateDeletionSchema state
  ) {
    super(source);
    _state = state;
  }
  
  public StateDeletionSchema getState () {
    return _state;
  }
}
