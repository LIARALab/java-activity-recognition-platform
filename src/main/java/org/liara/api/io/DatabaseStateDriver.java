package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
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

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DatabaseStateDriver
{
  @NonNull
  private final ApplicationEventPublisher _publisher;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseStateDriver (
    @NonNull final ApplicationEventPublisher publisher, @NonNull final EntityManager entityManager
  )
  {
    _publisher = publisher;
    _entityManager = entityManager;
  }

  @EventListener
  public void create (final ApplicationEntityEvent.@NonNull Create creation) {
    if (creation.getApplicationEntity() instanceof State) {
      @NonNull final State state = (State) creation.getApplicationEntity();

      _publisher.publishEvent(new StateEvent.WillBeCreated(this, state));
      _entityManager.persist(state);
      _publisher.publishEvent(new StateEvent.WasCreated(this, state));
    }
  }

  @EventListener
  public void mutate (final ApplicationEntityEvent.@NonNull Update mutation) {
    if (mutation.getApplicationEntity() instanceof State) {
      @NonNull final State state    = (State) mutation.getApplicationEntity();
      @NonNull final State oldState = _entityManager.find(state.getClass(), state.getIdentifier());

      _publisher.publishEvent(new StateEvent.WillBeMutated(this, oldState, state));
      _entityManager.persist(state);
      _publisher.publishEvent(new StateEvent.WasMutated(this, oldState, state));
    }
  }

  @EventListener
  public void delete (final ApplicationEntityEvent.@NonNull Delete deletion) {
    if (deletion.getApplicationEntity() instanceof State) {
      @NonNull final State state = (State) deletion.getApplicationEntity();

      _publisher.publishEvent(new StateEvent.WillBeDeleted(this, state));
      _entityManager.remove(state);
      _publisher.publishEvent(new StateEvent.WasDeleted(this, state));
    }
  }
}
