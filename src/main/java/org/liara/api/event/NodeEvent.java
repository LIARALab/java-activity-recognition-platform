package org.liara.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.schema.NodeSchema;
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

  public static class Create
    extends ApplicationEntityEvent
  {
    @NonNull
    private final NodeSchema _schema;

    public Create (@NonNull final Object source, @NonNull final NodeSchema schema) {
      super(source);

      _schema = Duplicator.duplicate(schema);
    }

    public @NonNull NodeSchema getSchema () {
      return Duplicator.duplicate(_schema);
    }
  }

  public static class WillBeCreated
    extends NodeEvent
  {
    public WillBeCreated (@NonNull final Object source, @NonNull final Node node) {
      super(source, node);
    }
  }

  public static class WasCreated
    extends NodeEvent
  {
    public WasCreated (@NonNull final Object source, @NonNull final Node node) {
      super(source, node);
    }
  }
}
