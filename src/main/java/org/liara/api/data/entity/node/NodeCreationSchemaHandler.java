package org.liara.api.data.entity.node;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.liara.api.data.collection.NodeCollection;
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
  private final EntityManager _entityManager;
  
  @NonNull
  private final NodeCollection _collection;
  
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;
  
  @Autowired
  public NodeCreationSchemaHandler (
    @NonNull final NodeCollection collection,
    @NonNull final ApplicationEventPublisher eventPublisher
  ) {
    _entityManager = collection.getManager();
    _collection = collection;
    _eventPublisher = eventPublisher;
  }

  @Transactional
  public Node handle (
    @NonNull final NodeCreationSchema schema
  ) {
    _eventPublisher.publishEvent(new NodeWillBeCreatedEvent(this, schema));
    final Node node = new Node(schema, _collection);

    if (schema.getParent() != null) {      
      updateNestedTreeBoundaries(node);
    }
    
    _collection.getManager().persist(node);
    _eventPublisher.publishEvent(new NodeWasCreatedEvent(this, node));
    
    return node;
  }

  private void updateNestedTreeBoundaries (
    @NonNull final Node node
  ) {
    _entityManager.createQuery(
      "UPDATE Node SET _setStart = _setStart + 2 WHERE _setStart > :parentSetEnd"
    ).setParameter(
      "parentSetEnd", node.getSetStart()
    ).executeUpdate();
    
    _entityManager.createQuery(
      "UPDATE Node SET _setEnd = _setEnd + 2 WHERE _setEnd >= :parentSetEnd"
    ).setParameter(
      "parentSetEnd", node.getSetEnd()
    ).executeUpdate();
  }
}
