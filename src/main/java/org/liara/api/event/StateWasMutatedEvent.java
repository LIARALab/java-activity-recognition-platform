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
  private final StateSnapshot _oldValue;
  
  @NonNull
  private final StateSnapshot _newValue;
  
  /**
   * 
   * @param source
   * @param state
   */
  public StateWasMutatedEvent(
    @NonNull final Object source,
    @NonNull final StateSnapshot oldValue,
    @NonNull final State newValue
  ) {
    super(source);
    _oldValue = oldValue;
    _newValue = newValue.snapshot();
  }

  public StateSnapshot getNewValue () {
    return _newValue;
  }
  
  public StateSnapshot getOldValue () {
    return _oldValue;
  }
}
