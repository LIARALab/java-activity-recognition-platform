package org.liara.api.event;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.NodeSnapshot;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class NodeWasCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 1561166985562831238L;
  
  @NonNull
  private final NodeSnapshot _node;
  
  public NodeWasCreatedEvent(
    @NonNull final Object source,
    @NonNull final Node node
  ) {
    super(source);
    _node = node.snapshot();
  }
  
  public NodeSnapshot getNode () {
    return _node;
  }
}
