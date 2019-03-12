package org.liara.api.event.node;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.utils.Duplicator;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public abstract class NodeEvent
  extends ApplicationEvent
{
  @NonNull
  private final Node _node;

  public NodeEvent (
    @NonNull final Object source,
    @NonNull final Node node
  ) {
    super(source);
    _node = Duplicator.duplicate(node);
  }

  public @NonNull Node getNode () {
    return Duplicator.duplicate(_node);
  }

  @Override
  public int hashCode () {
    return Objects.hash(getNode());
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (getClass().isInstance(other)) {
      @NonNull final NodeEvent otherEvent = (NodeEvent) other;

      return Objects.equals(getSource(), otherEvent.getSource()) &&
             Objects.equals(getTimestamp(), otherEvent.getTimestamp()) &&
             Objects.equals(getNode(), otherEvent.getNode());
    }

    return false;
  }

}
