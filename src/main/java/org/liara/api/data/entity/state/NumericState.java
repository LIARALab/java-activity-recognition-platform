package org.liara.api.data.entity.state;

import org.springframework.lang.NonNull;

public abstract class NumericState extends State
{
  public NumericState() {
    super();
  }

  public NumericState( 
    @NonNull final StateCreationSchema schema
  ) { super(schema); }
  
  public abstract Number getNumber ();

  @Override
  public NumericStateSnapshot snapshot () {
    throw new UnsupportedOperationException();
  }
}
