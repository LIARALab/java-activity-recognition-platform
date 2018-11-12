package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Node;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.NodeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

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

  public void create (final NodeEvent.@NonNull Create creation) {
    @NonNull final Node node = new Node(creation.getSchema());

    _eventPublisher.publishEvent(new ApplicationEntityEvent.Create(node));

    if (creation.getSchema().getParent() == null) {
      _repository.attachChild(node);
    } else {
      _repository.attachChild(node, _repository.find(creation.getSchema().getParent()).get());
    }

    creation.getSchema().setIdentifier(node.getIdentifier());
    _eventPublisher.publishEvent(new NodeEvent.WasCreated(this, node));
  }

  public void willCreate (final ApplicationEntityEvent.@NonNull WillCreate creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof Node) {
        _eventPublisher.publishEvent(new NodeEvent.WillBeCreated(this, (Node) entity));
      }
    }
  }
}
