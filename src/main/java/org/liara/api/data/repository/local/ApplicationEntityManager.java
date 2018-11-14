package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.utils.InstanceDescriptor;

import java.util.*;

public class ApplicationEntityManager
{
  @NonNull
  private final ApplicationEntityCollection _entities;

  @NonNull
  private final Set<@NonNull ApplicationEntityManagerListener> _listeners;

  @NonNull
  private final Map<@NonNull InstanceDescriptor<? extends ApplicationEntityManagerListener>,
                       @NonNull ApplicationEntityManagerListener> _repositories;

  public ApplicationEntityManager () {
    _entities = new ApplicationEntityCollection();
    _listeners = Collections.newSetFromMap(new WeakHashMap<>());
    _repositories = new HashMap<>();
  }

  public <Entity extends ApplicationEntity> @NonNull Optional<Entity> find (
    @NonNull final Class<Entity> type, @NonNull final Long identifier
  ) {
    return _entities.contains(type, identifier) ? Optional.of(_entities.get(type, identifier)) : Optional.empty();
  }

  public <Entity extends ApplicationEntity> @NonNull Entity getAt (
    @NonNull final Class<Entity> type, @NonNull final Long identifier
  )
  {
    return find(
      type,
      identifier
    ).orElseThrow();
  }

  public <Entity extends ApplicationEntity> @NonNull List<@NonNull Entity> findAll (@NonNull final Class<Entity> type) {
    return _entities.get(type);
  }

  public void merge (@NonNull final ApplicationEntity newEntity) {
    @Nullable final ApplicationEntity oldEntity = _entities.contains(newEntity) ? _entities.get(newEntity) : null;
    _entities.add(newEntity);

    for (@NonNull final ApplicationEntityManagerListener listener : _listeners) {
      listener.onUpdate(oldEntity, newEntity);
    }
  }

  public void addListener (@NonNull final ApplicationEntityManagerListener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
      listener.setParent(this);

      for (@NonNull final ApplicationEntity entity : _entities) {
        listener.onUpdate(null, entity);
      }
    }
  }

  public void removeListener (@NonNull final ApplicationEntityManagerListener listener) {
    if (_listeners.contains(listener)) {
      _listeners.remove(listener);
      listener.setParent(null);

      for (@NonNull final ApplicationEntity entity : _entities) {
        listener.onRemove(entity);
      }
    }
  }

  public void remove (@NonNull final ApplicationEntity entity) {
    if (_entities.contains(entity)) {
      for (final ApplicationEntityManagerListener listener : _listeners) {
        listener.onRemove(entity);
      }

      _entities.remove(entity);
    }
  }

  @NonNull
  public <Entity extends ApplicationEntity> List<@NonNull Entity> get (@NonNull final Class<Entity> type) {
    return _entities.get(type);
  }

  @NonNull
  public ApplicationEntity get (@NonNull final ApplicationEntity entity) {return _entities.get(entity);}

  public <Entity extends ApplicationEntity> @NonNull Entity get (
    @NonNull final Class<Entity> type, @NonNull final Long identifier
  )
  {return _entities.get(type, identifier);}

  public <Repository extends ApplicationEntityManagerListener> @NonNull Repository repository (
    @NonNull final Class<Repository> type, @Nullable final Object... parameters
  )
  {
    @NonNull final InstanceDescriptor<Repository> descriptor = new InstanceDescriptor<>(type, parameters);

    if (!_repositories.containsKey(descriptor)) {
      _repositories.put(descriptor, descriptor.instantiate());
      _repositories.get(descriptor).setParent(this);
    }

    return type.cast(_repositories.get(descriptor));
  }
  
  public boolean contains (@NonNull final ApplicationEntity entity) {
    return _entities.contains(entity);
  }

  public boolean contains (@NonNull final Class<? extends ApplicationEntity> type, @NonNull final Long identifier) {
    return _entities.contains(type, identifier);
  }

  public void clear () {
    for (@NonNull final ApplicationEntity entity : _entities) {
      for (@NonNull final ApplicationEntityManagerListener listener : _listeners) {
        listener.onRemove(entity);
      }
    }

    _entities.clear();
  }

  public void clear (@NonNull final Class<? extends ApplicationEntity> type) {
    for (@NonNull final ApplicationEntity entity : _entities.get(type)) {
      for (@NonNull final ApplicationEntityManagerListener listener : _listeners) {
        listener.onRemove(entity);
      }
    }

    _entities.clear(type);
  }

  public int getSize () {
    return _entities.getSize();
  }

  public int getSize (@NonNull final Class<? extends ApplicationEntity> type) {
    return _entities.getSize(type);
  }
}
