package org.liara.api.data.tree;

import jdk.internal.jline.internal.Nullable;
import org.springframework.lang.NonNull;

public interface NestedSet
{
  @Nullable
  Long getIdentifier ();

  void setIdentifier (@Nullable final Long identifier);

  @NonNull
  NestedSetCoordinates getCoordinates ();

  void setCoordinates (@Nullable final NestedSetCoordinates coordinates);
}
