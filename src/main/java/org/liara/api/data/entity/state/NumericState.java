package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class NumericState extends State
{
  public NumericState () {
    super();
  }

  public NumericState (@NonNull final NumericState toCopy) {
    super(toCopy);
  }

  public abstract @Nullable Number getValue ();
}
