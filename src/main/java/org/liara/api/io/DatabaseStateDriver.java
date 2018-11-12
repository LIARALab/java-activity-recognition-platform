package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.state.State;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.api.event.StateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseStateDriver
{
  @NonNull
  private final ApplicationEventPublisher _publisher;

  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final WeakHashMap<@NonNull State, @NonNull State> _oldStates;

  @Autowired
  public DatabaseStateDriver (
    @NonNull final ApplicationEventPublisher publisher, @NonNull final EntityManager entityManager
  )
  {
    _publisher = publisher;
    _oldStates = new WeakHashMap<>();
    _entityManager = entityManager;
  }

  @EventListener
  public void willCreate (final ApplicationEntityEvent.@NonNull WillCreate creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof State) _publisher.publishEvent(new StateEvent.WillBeCreated(this, (State) entity));
    }
  }

  @EventListener
  public void didCreate (final ApplicationEntityEvent.@NonNull DidCreate creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) {
      if (entity instanceof State) _publisher.publishEvent(new StateEvent.WasCreated(this, (State) entity));
    }
  }

  @EventListener
  public void willDelete (final ApplicationEntityEvent.@NonNull WillDelete deletion) {
    for (@NonNull final ApplicationEntity entity : deletion.getEntities()) {
      if (entity instanceof State) _publisher.publishEvent(new StateEvent.WillBeDeleted(this, (State) entity));
    }
  }

  @EventListener
  public void didDelete (final ApplicationEntityEvent.@NonNull DidDelete deletion) {
    for (@NonNull final ApplicationEntity entity : deletion.getEntities()) {
      if (entity instanceof State) _publisher.publishEvent(new StateEvent.WasDeleted(this, (State) entity));
    }
  }

  @EventListener
  public void willUpdate (final ApplicationEntityEvent.@NonNull WillUpdate mutation) {
    for (@NonNull final ApplicationEntity entity : mutation.getEntities()) {
      if (entity instanceof State) {
        @NonNull final State newState = (State) entity;
        @NonNull final State oldState = newState.getReference().resolve(_entityManager);

        _oldStates.put(newState, oldState);
        _publisher.publishEvent(new StateEvent.WillBeMutated(this, oldState, newState));
      }
    }
  }

  @EventListener
  public void didUpdate (final ApplicationEntityEvent.@NonNull DidUpdate mutation) {
    for (@NonNull final ApplicationEntity entity : mutation.getEntities()) {
      if (entity instanceof State) {
        @NonNull final State newState = (State) entity;
        @NonNull final State oldState = _oldStates.get(newState);

        _oldStates.remove(newState, oldState);
        _publisher.publishEvent(new StateEvent.WasMutated(this, oldState, newState));
      }
    }
  }
}
