package org.liara.api.event;

import org.liara.api.data.entity.Node;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class NodeWasCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = 1561166985562831238L;
  
  @NonNull
  private final Node _node;
  
  public NodeWasCreatedEvent(
    @NonNull final Object source,
    @NonNull final Node node
  ) {
    super(source);
    _node = node.clone();
  }

  public @NonNull
  Node getNode () {
    return _node.clone();
  }
}
