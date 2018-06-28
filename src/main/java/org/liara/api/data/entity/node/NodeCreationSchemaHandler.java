package org.liara.api.data.entity.node;

import javax.transaction.Transactional;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.event.NodeWasCreatedEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

@SchemaHandler(NodeCreationSchema.class)
public class NodeCreationSchemaHandler
{  
  @NonNull
  private final DatabaseNodeTree _tree;
  
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @Autowired
  public NodeCreationSchemaHandler (
    @NonNull final DatabaseNodeTree tree,
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _tree = tree;
    _eventPublisher = eventPublisher;
  }

  @Transactional
  public Node handle (
    @NonNull final NodeCreationSchema schema
  ) throws EntityNotFoundException {
    _eventPublisher.publishEvent(new NodeWillBeCreatedEvent(this, schema));
    final Node node = new Node(schema);

    _tree.addNode(
      node, _tree.getNode(schema.getParent())
    );
    
    _eventPublisher.publishEvent(new NodeWasCreatedEvent(this, node));
    return node;
  }
}
