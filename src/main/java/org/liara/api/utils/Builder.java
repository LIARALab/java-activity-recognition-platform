package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public interface Builder<Output>
{
  static <Value> @NonNull Value require (@Nullable final Value value) {
    return Objects.requireNonNull(value);
  }

  @NonNull Class<Output> getOutputClass ();

  @NonNull Output build ();
}
