package org.liara.api.event;

import org.liara.api.data.entity.state.State;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StateWasMutatedEvent extends ApplicationEvent
{  
  /**
   * 
   */
  private static final long serialVersionUID = 8116556813252780522L;
  
  @NonNull
  private final State _oldValue;
  
  @NonNull
  private final State _newValue;
  
  /**
   * 
   * @param source
   */
  public StateWasMutatedEvent(
    @NonNull final Object source, @NonNull final State oldValue,
    @NonNull final State newValue
  ) {
    super(source);
    _oldValue = oldValue.clone();
    _newValue = newValue.clone();
  }

  public State getNewValue () {
    return _newValue;
  }

  public State getOldValue () {
    return _oldValue;
  }
}
