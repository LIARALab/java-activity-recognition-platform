package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;

import org.springframework.lang.NonNull;

public class ActivityStateSnapshot extends StateSnapshot
{
  @NonNull
  private final String _tag;

  @NonNull
  private final ZonedDateTime _start;

  @NonNull
  private final ZonedDateTime _end;

  @NonNull
  private final Long _node;
  
  public ActivityStateSnapshot(@NonNull final ActivityState model) {
    super(model);
    
    _tag = model.getTag();
    _start = model.getStart();
    _end = model.getEnd();
    _node = model.getNodeIdentifier();
  }
  
  public ActivityStateSnapshot(@NonNull final ActivityStateSnapshot toCopy) {
    super(toCopy);
    
    _tag = toCopy.getTag();
    _start = toCopy.getStart();
    _end = toCopy.getEnd();
    _node = toCopy.getNode();
  }

  public String getTag () {
    return _tag;
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
  public ActivityStateSnapshot clone () {
    return new ActivityStateSnapshot(this);
  }
  
  @Override
  public ActivityState getModel () {
    return (ActivityState) super.getModel();
  }
}
