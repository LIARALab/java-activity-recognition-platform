package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.utils.CloneMemory;

public abstract class NumericState extends State
{
  public NumericState () {
    super();
  }

  public NumericState (@NonNull final NumericState toCopy, @NonNull final CloneMemory clones) {
    super(toCopy, clones);
  }

  public abstract @Nullable Number getValue ();
}
