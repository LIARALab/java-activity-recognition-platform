package org.liara.api.data.entity.node;

import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.event.NodeWasCreatedEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.WeakHashMap;

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
  ) {
    _eventPublisher.publishEvent(new NodeWillBeCreatedEvent(this, schema));
    
    final Node node = new Node(schema);
    node.setCreationDate(ZonedDateTime.now());
    node.setUpdateDate(ZonedDateTime.now());
    node.setDeletionDate(null);

    final DatabaseNodeTree tree = getTree(manager);
    tree.addNode(node, schema.getParent().resolve(manager));
   
    _eventPublisher.publishEvent(new NodeWasCreatedEvent(this, node));
    return node;
  }
}
