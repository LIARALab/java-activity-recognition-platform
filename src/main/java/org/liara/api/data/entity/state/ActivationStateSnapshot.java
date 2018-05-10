package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ActivationStateSnapshot extends StateSnapshot
{
  @NonNull
  private final Long _startState;
  
  @Nullable
  private final Long _endState;
  
  @NonNull
  private final Long _node;
  
  public ActivationStateSnapshot(@NonNull final ActivationStateSnapshot toCopy) {
    super(toCopy);
    
    _startState = toCopy.getStartState();
    _endState = toCopy.getEndState();
    _node = toCopy.getNode();
  }

  public ActivationStateSnapshot(@NonNull final ActivationState model) {
    super(model);
    
    _startState = model.getStartStateIdentifier();
    _endState = model.getEndStateIdentifier();
    _node = model.getNodeIdentifier();
  }
  
  public Long getStartState () {
    return _startState;
  }
  
  public Long getEndState () {
    return _endState;
  }
  
  public Long getNode () {
    return _node;
  }
  
  @Override
  public ActivationStateSnapshot clone () {
    return new ActivationStateSnapshot(this);
  }
  
  @Override
  public ActivationState getModel () {
    return (ActivationState) super.getModel();
  }
}
