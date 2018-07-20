package org.liara.api.data.entity.state;

public abstract class NumericState extends State
{
  public NumericState() {
    super();
  }
  
  public abstract Number getNumber ();

  @Override
  public NumericStateSnapshot snapshot () {
    throw new UnsupportedOperationException();
  }
}
