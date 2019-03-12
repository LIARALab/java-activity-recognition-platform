package org.liara.api.event.node;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;

public class DidCreateNodeEvent
  extends NodeEvent
{
  public DidCreateNodeEvent (@NonNull final Object source, @NonNull final Node node) {
    super(source, node);
  }
}
