package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.series.SeriesManager;
import org.liara.api.event.entity.*;
import org.liara.api.event.state.*;
import org.liara.api.utils.Duplicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;
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

  @NonNull
  private final SeriesManager _seriesManager;

  @Autowired
  public DatabaseStateDriver (
    @NonNull final ApplicationEventPublisher publisher,
    @NonNull final EntityManager entityManager,
    @NonNull final SeriesManager seriesManager
  )
  {
    _publisher = publisher;
    _seriesManager = seriesManager;
    _oldStates = new WeakHashMap<>();
    _entityManager = entityManager;
  }

  @EventListener
  public void willCreate (final WillCreateApplicationEntityEvent creation) {
    if (creation.getEntity() instanceof State) {
      _publisher.publishEvent(new WillCreateStateEvent(this, (State) creation.getEntity()));
    }
  }

  @EventListener
  public void didCreate (final DidCreateApplicationEntityEvent creation) {
    if (creation.getEntity() instanceof State) {
      _seriesManager.forget(Objects.requireNonNull(((State) creation.getEntity()).getSensorIdentifier()));
      _publisher.publishEvent(new DidCreateStateEvent(this, (State) creation.getEntity()));
    }
  }

  @EventListener
  public void willDelete (final WillDeleteApplicationEntityEvent deletion) {
    if (deletion.getEntity() instanceof State) {
      _publisher.publishEvent(new WillDeleteStateEvent(this, (State) deletion.getEntity()));
    }
  }

  @EventListener
  public void didDelete (final DidDeleteApplicationEntityEvent deletion) {
    if (deletion.getEntity() instanceof State) {
      _seriesManager.forget(Objects.requireNonNull(((State) deletion.getEntity()).getSensorIdentifier()));
      _publisher.publishEvent(new DidDeleteStateEvent(this, (State) deletion.getEntity()));
    }
  }

  @EventListener
  public void willUpdate (final WillUpdateApplicationEntityEvent mutation) {
    if (mutation.getEntity() instanceof State) {
      @NonNull final State newState = (State) mutation.getEntity();
      @NonNull final State oldState = Duplicator.duplicate(
        _entityManager.find(
          newState.getClass(),
          newState.getIdentifier()
        )
      );

      _oldStates.put(newState, oldState);
      _publisher.publishEvent(new WillUpdateStateEvent(this, oldState, newState));
    }
  }

  @EventListener
  public void didUpdate (final DidUpdateApplicationEntityEvent mutation) {
    if (mutation.getEntity() instanceof State) {
      @NonNull final State newState = (State) mutation.getEntity();
      @NonNull final State oldState = _oldStates.get(newState);

      _seriesManager.forget(Objects.requireNonNull(newState.getSensorIdentifier()));

      _oldStates.remove(newState);
      _publisher.publishEvent(new DidUpdateStateEvent(this, oldState, newState));
    }
  }
}
