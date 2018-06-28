package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ActivationStateSnapshot extends StateSnapshot
{
  @NonNull
  private final ZonedDateTime _start;
  
  @Nullable
  private final ZonedDateTime _end;
  
  @NonNull
  private final Long _node;
  
  public ActivationStateSnapshot(@NonNull final ActivationStateSnapshot toCopy) {
    super(toCopy);
    
    _start = toCopy.getStart();
    _end = toCopy.getEnd();
    _node = toCopy.getNode();
  }

  public ActivationStateSnapshot(@NonNull final ActivationState model) {
    super(model);
    
    _start = model.getStart();
    _end = model.getEnd();
    _node = model.getNodeIdentifier();
  }
  
  public ZonedDateTime getStart () {
    return _start;
  }
  
  public ZonedDateTime getEnd () {
    return _end;
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
