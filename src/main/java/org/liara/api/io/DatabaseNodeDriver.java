package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.schema.NodeSchema;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.database.DatabaseNodeRepository;
import org.liara.api.event.entity.DidCreateApplicationEntityEvent;
import org.liara.api.event.entity.WillCreateApplicationEntityEvent;
import org.liara.api.event.node.CreateNodeEvent;
import org.liara.api.event.node.DidCreateNodeEvent;
import org.liara.api.event.node.WillCreateNodeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @TODO use generator entity manager
 */
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseNodeDriver
{
  @NonNull
  private final APIEventPublisher _eventPublisher;

  @NonNull
  private final NodeRepository _repository;

  @NonNull
  private final Map<@NonNull Integer, @NonNull NodeSchema> _inCreation = new HashMap<>();

  @Autowired
  public DatabaseNodeDriver (
    @NonNull final APIEventPublisher eventPublisher,
    @NonNull final DatabaseNodeRepository repository
  ) {
    _eventPublisher = eventPublisher;
    _repository = repository;
  }

  @EventListener
  public void create (@NonNull final CreateNodeEvent creation) {
    try {
      _eventPublisher.willConsume(creation);
      @NonNull final Node node = new Node();
      node.setName(creation.getSchema().getName());
      node.getCoordinates().set(1, 2, 1);
      _inCreation.put(System.identityHashCode(node), creation.getSchema());
      _eventPublisher.create(node);
      _eventPublisher.didConsume(creation);
    } catch (@NonNull final Throwable throwable) {
      _eventPublisher.didReject(creation);
      throw new Error("Error during node creation event handling.", throwable);
    }
  }

  @EventListener
  public void didCreate (@NonNull final DidCreateApplicationEntityEvent creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof Node) {
        didCreateNode((Node) entity);
      }
    }
  }

  private void didCreateNode (@NonNull final Node node) {
    @NonNull final NodeSchema schema = _inCreation.get(System.identityHashCode(node));
    _inCreation.remove(System.identityHashCode(node));

    if (schema.getParent() == null) {
      _repository.attachChild(node);
    } else {
      _repository.attachChild(node, _repository.find(schema.getParent()).orElseThrow());
    }

    schema.setIdentifier(node.getIdentifier());
    _eventPublisher.getApplicationEventPublisher().publishEvent(
      new DidCreateNodeEvent(this, node)
    );
  }

  @EventListener
  public void willCreate (@NonNull final WillCreateApplicationEntityEvent creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof Node) {
        _eventPublisher.getApplicationEventPublisher().publishEvent(
          new WillCreateNodeEvent(this, (Node) entity)
        );
      }
    }
  }
}
