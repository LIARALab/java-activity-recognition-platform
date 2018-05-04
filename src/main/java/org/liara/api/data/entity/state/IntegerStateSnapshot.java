package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;

public class IntegerStateSnapshot extends StateSnapshot
{
  private final int _value;
  
  public IntegerStateSnapshot(@NonNull final IntegerStateSnapshot toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
  }

  public IntegerStateSnapshot(@NonNull final IntegerState model) {
    super(model);
    _value = model.getValue();
  }
  
  public int getValue () {
    return _value;
  }
  
  @Override
  public IntegerStateSnapshot clone () {
    return new IntegerStateSnapshot(this);
  }
  
  @Override
  public IntegerState getModel() {
    return (IntegerState) super.getModel();
  }
}
