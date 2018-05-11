package org.liara.api.data.entity.state.activity;

import org.liara.api.data.entity.state.ActivityStateSnapshot;
import org.springframework.lang.NonNull;

public class BaseSleepingActivityStateSnapshot extends ActivityStateSnapshot
{
  @NonNull
  private final Long _relatedPresence;
  
  public BaseSleepingActivityStateSnapshot(@NonNull final BaseSleepingActivityState model) {
    super(model);
    
    _relatedPresence = model.getRelatedPresenceIdentifier();
  }
  
  public BaseSleepingActivityStateSnapshot(@NonNull final BaseSleepingActivityStateSnapshot toCopy) {
    super(toCopy);
    
    _relatedPresence = toCopy.getRelatedPresence();
  }

  public Long getRelatedPresence () {
    return _relatedPresence;
  }
  
  @Override
  public BaseSleepingActivityStateSnapshot clone () {
    return new BaseSleepingActivityStateSnapshot(this);
  }
  
  @Override
  public BaseSleepingActivityState getModel () {
    return (BaseSleepingActivityState) super.getModel();
  }
}
