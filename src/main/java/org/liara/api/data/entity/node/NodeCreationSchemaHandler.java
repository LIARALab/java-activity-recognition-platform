package org.liara.api.data.entity.node;

import java.util.WeakHashMap;

import javax.persistence.EntityManager;
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
  private final ApplicationEventPublisher _eventPublisher;
  
  @Autowired
  public NodeCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _eventPublisher = eventPublisher;
  }
  
  @NonNull
  private final WeakHashMap<EntityManager, DatabaseNodeTree> _trees = new WeakHashMap<>();

  private DatabaseNodeTree getTree (@NonNull final EntityManager manager) {
    if (!_trees.containsKey(manager)) {
      _trees.put(manager, new DatabaseNodeTree(manager));
    }
    
    return _trees.get(manager);
  }
  
  @Transactional
  public Node handle (
    @NonNull final EntityManager manager,
    @NonNull final NodeCreationSchema schema
  ) throws EntityNotFoundException {
    _eventPublisher.publishEvent(new NodeWillBeCreatedEvent(this, schema));
    final Node node = new Node(schema);
    final DatabaseNodeTree tree = getTree(manager);
    
    tree.addNode(
      node, tree.getNode(schema.getParent())
    );
    
    _eventPublisher.publishEvent(new NodeWasCreatedEvent(this, node));
    return node;
  }
}
