package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface DeeplyCloneable
  extends Cloneable
{
  default @NonNull Object clone ()
  throws CloneNotSupportedException
  {
    return clone(new CloneMemory());
  }

  @NonNull Object clone (@NonNull final CloneMemory memory)
  throws CloneNotSupportedException;
}
