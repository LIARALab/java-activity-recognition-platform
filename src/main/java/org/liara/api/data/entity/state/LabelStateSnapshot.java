package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;

public class LabelStateSnapshot
  extends StateSnapshot
{
  @NonNull
  private final String _tag;

  @NonNull
  private final ZonedDateTime _start;

  @NonNull
  private final ZonedDateTime _end;

  @NonNull
  private final Long _node;

  public LabelStateSnapshot (@NonNull final LabelState model) {
    super(model);
    
    _tag = model.getTag();
    _start = model.getStart();
    _end = model.getEnd();
    _node = model.getNodeIdentifier();
  }

  public LabelStateSnapshot (@NonNull final LabelStateSnapshot toCopy) {
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
  public LabelStateSnapshot clone () {
    return new LabelStateSnapshot(this);
  }
  
  @Override
  public LabelState getModel () {
    return (LabelState) super.getModel();
  }
}
