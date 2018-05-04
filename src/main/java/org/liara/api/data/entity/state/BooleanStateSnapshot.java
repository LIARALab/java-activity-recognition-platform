package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;

public class BooleanStateSnapshot extends StateSnapshot
{
  private final boolean _value;
  
  public BooleanStateSnapshot(@NonNull final BooleanStateSnapshot toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
  }

  public BooleanStateSnapshot(@NonNull final BooleanState model) {
    super(model);
    _value = model.getValue();
  }
  
  public boolean getValue () {
    return _value;
  }
  
  @Override
  public BooleanStateSnapshot clone () {
    return new BooleanStateSnapshot(this);
  }
  
  @Override
  public BooleanState getModel () {
    return (BooleanState) super.getModel();
  }
}
