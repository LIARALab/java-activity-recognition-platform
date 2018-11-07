package org.liara.api.data.handler;

import org.liara.api.data.entity.Node;
import org.liara.api.data.schema.NodeCreationSchema;
import org.liara.api.data.schema.SchemaHandler;
import org.liara.api.data.tree.NestedSetRepository;
import org.liara.api.event.NodeEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;

@SchemaHandler(NodeCreationSchema.class)
public class NodeCreationSchemaHandler
{  
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;

  @NonNull
  private final NestedSetRepository _repository;
  
  @Autowired
  public NodeCreationSchemaHandler (
    @NonNull final ApplicationEventPublisher eventPublisher, @NonNull final NestedSetRepository repository
  ) {
    _eventPublisher = eventPublisher;
    _repository = repository;
  }

  @Transactional
  public Node handle (
    @NonNull final EntityManager entityManager, @NonNull final NodeCreationSchema creationSchema
  ) {
    _eventPublisher.publishEvent(new NodeWillBeCreatedEvent(this, creationSchema));

    final Node node = new Node();
    node.setCreationDate(ZonedDateTime.now());
    node.setUpdateDate(ZonedDateTime.now());
    node.setDeletionDate(null);
    node.setUUID(creationSchema.getUUID() == null ? UUID.randomUUID() : creationSchema.getUUID());
    node.setName(creationSchema.getName());
    node.setType(creationSchema.getType());

    entityManager.persist(node);
    _repository.attachChild(node,
                            (creationSchema.getParent() == null) ? null
                                                                 : creationSchema.getParent().resolve(entityManager)
    );

    _eventPublisher.publishEvent(new NodeEvent(this, node));
    return node;
  }
}
