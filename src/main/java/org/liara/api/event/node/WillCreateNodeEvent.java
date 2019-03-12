package org.liara.api.event.node;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;

public class WillCreateNodeEvent
  extends NodeEvent
{
  public WillCreateNodeEvent (@NonNull final Object source, @NonNull final Node node) {
    super(source, node);
  }
}
