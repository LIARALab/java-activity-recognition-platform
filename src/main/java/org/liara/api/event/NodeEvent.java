package org.liara.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.utils.Duplicator;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class NodeEvent
  extends ApplicationEvent
{
  public NodeEvent (
    @NonNull final Object source,
    @NonNull final Node node
  ) {
    super(source);
    _node = Duplicator.duplicate(node);
  }

  public NodeEvent (@NonNull final NodeEvent toCopy) {
    super(toCopy);
    _node = toCopy.getNode();
  }

  /**
   *
   */
  private static final long serialVersionUID = 1561166985562831238L;

  @NonNull
  private final Node _node;

  public @NonNull Node getNode () {
    return Duplicator.duplicate(_node);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_node);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (getClass().isInstance(other)) {
      @NonNull final NodeEvent otherEvent = (NodeEvent) other;

      return Objects.equals(getSource(), otherEvent.getSource()) &&
             Objects.equals(getTimestamp(), otherEvent.getTimestamp()) && Objects.equals(_node, otherEvent.getNode());
    }

    return false;
  }

  public static class WillBeCreated
    extends NodeEvent
  {
    public WillBeCreated (@NonNull final Object source, @NonNull final Node node) {
      super(source, node);
    }

    public WillBeCreated (@NonNull final WillBeCreated toCopy) {
      super(toCopy);
    }
  }

  public static class WasCreated
    extends NodeEvent
  {
    public WasCreated (@NonNull final Object source, @NonNull final Node node) {
      super(source, node);
    }

    public WasCreated (@NonNull final WasCreated toCopy) {
      super(toCopy);
    }
  }
}
