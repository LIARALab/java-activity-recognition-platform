package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;

public class DoubleStateSnapshot extends StateSnapshot
{
  private final double _value;
  
  public DoubleStateSnapshot(@NonNull final DoubleStateSnapshot toCopy) {
    super(toCopy);
    _value = toCopy.getValue();
  }

  public DoubleStateSnapshot(@NonNull final DoubleState model) {
    super(model);
    _value = model.getValue();
  }
  
  public double getValue () {
    return _value;
  }
  
  @Override
  public DoubleStateSnapshot clone () {
    return new DoubleStateSnapshot(this);
  }
  
  @Override
  public DoubleState getModel () {
    return (DoubleState) super.getModel();
  }
}
