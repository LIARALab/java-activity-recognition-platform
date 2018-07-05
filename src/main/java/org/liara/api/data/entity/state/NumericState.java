package org.liara.api.data.entity.state;

import javax.persistence.EntityManager;

import org.springframework.lang.NonNull;

public abstract class NumericState extends State
{
  public NumericState() {
    super();
  }

  public NumericState( 
    @NonNull final EntityManager manager,
    @NonNull final StateCreationSchema schema
  ) { super(manager, schema); }
  
  public abstract Number getNumber ();

  @Override
  public NumericStateSnapshot snapshot () {
    throw new UnsupportedOperationException();
  }
}
