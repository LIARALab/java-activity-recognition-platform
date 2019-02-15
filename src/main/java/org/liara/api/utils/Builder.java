package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Builder<Output>
{
  @NonNull Class<Output> getOutputClass ();

  @NonNull Output build ();
}
