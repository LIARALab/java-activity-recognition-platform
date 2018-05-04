package org.liara.api.event;

import org.liara.api.data.entity.node.NodeCreationSchema;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class NodeWillBeCreatedEvent extends ApplicationEvent
{
  /**
   * 
   */
  private static final long serialVersionUID = -5512592927163401545L;

  @NonNull
  private final NodeCreationSchema _node;
  
  public NodeWillBeCreatedEvent(
    @NonNull final Object source,
    @NonNull final NodeCreationSchema node
  ) {
    super(source);
    _node = node;
  }
  
  public NodeCreationSchema getNode () {
    return _node;
  }
}
