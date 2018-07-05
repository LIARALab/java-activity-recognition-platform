package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;

public abstract class NumericStateSnapshot extends StateSnapshot
{
  @NonNull
  private final Number _number;
  
  public NumericStateSnapshot(@NonNull final NumericState model) {
    super(model);
    
    _number = model.getNumber();
  }
  
  public NumericStateSnapshot(@NonNull final NumericStateSnapshot toCopy) {
    super(toCopy);
    
    _number = toCopy.getNumber();
  }

  public Number getNumber () {
    return _number;
  }

  @Override
  public NumericStateSnapshot clone () {
    throw new UnsupportedOperationException();
  }
}
