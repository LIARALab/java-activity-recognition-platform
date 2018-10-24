package org.liara.api.data.tree;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface NestedSet
  extends Cloneable
{
  @Nullable
  Long getIdentifier ();

  void setIdentifier (@Nullable final Long identifier);

  @NonNull
  NestedSetCoordinates getCoordinates ();

  void setCoordinates (@Nullable final NestedSetCoordinates coordinates);
}
