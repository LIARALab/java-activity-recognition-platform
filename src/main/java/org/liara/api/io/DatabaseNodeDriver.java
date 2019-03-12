package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Node;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.event.entity.CreateApplicationEntityEvent;
import org.liara.api.event.entity.WillCreateApplicationEntityEvent;
import org.liara.api.event.node.CreateNodeEvent;
import org.liara.api.event.node.DidCreateNodeEvent;
import org.liara.api.event.node.WillCreateNodeEvent;
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
  public void create (final CreateNodeEvent creation) {
    @NonNull final Node node = new Node();

    node.setName(creation.getSchema().getName());
    node.getCoordinates().set(1, 2, 1);
    _eventPublisher.publishEvent(new CreateApplicationEntityEvent(this, node));

    if (creation.getSchema().getParent() == null) {
      _repository.attachChild(node);
    } else {
      _repository.attachChild(node, _repository.find(creation.getSchema().getParent()).orElseThrow());
    }

    creation.getSchema().setIdentifier(node.getIdentifier());
    _eventPublisher.publishEvent(new DidCreateNodeEvent(this, node));
  }

  @Transactional
  @EventListener
  public void willCreate (final WillCreateApplicationEntityEvent creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof Node) {
        _eventPublisher.publishEvent(new WillCreateNodeEvent(this, (Node) entity));
      }
    }
  }
}
