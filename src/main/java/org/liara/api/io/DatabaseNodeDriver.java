package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.NodeSchema;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.NodeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseNodeDriver
{
  @NonNull
  private final ApplicationEventPublisher _eventPublisher;

  @NonNull
  private final NodeRepository _repository;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseNodeDriver (
    @NonNull final ApplicationEventPublisher eventPublisher,
    @NonNull final NodeRepository repository,
    @NonNull final EntityManager entityManager
  )
  {
    _eventPublisher = eventPublisher;
    _repository = repository;
    _entityManager = entityManager;
  }

  @Transactional
  @EventListener
  public void create (final ApplicationEntityEvent.@NonNull Create creation) {
    if (creation.getApplicationEntity() instanceof NodeSchema) {
      @NonNull final NodeSchema schema = (NodeSchema) creation.getApplicationEntity();
      @NonNull final Node       node   = new Node(schema);

      _eventPublisher.publishEvent(new NodeEvent.WillBeCreated(this, node));
      _entityManager.persist(node);

      if (schema.getParent() == null) {
        _repository.attachChild(node);
      } else {
        _repository.attachChild(node, _repository.find(schema.getParent()).get());
      }

      schema.setIdentifier(node.getIdentifier());
      _eventPublisher.publishEvent(new NodeEvent.WasCreated(this, node));
    }
  }
}
