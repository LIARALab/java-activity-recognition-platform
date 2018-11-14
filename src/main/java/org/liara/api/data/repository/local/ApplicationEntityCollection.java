package org.liara.api.data.repository.local;

import com.google.common.collect.Iterators;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.utils.Duplicator;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationEntityCollection
  implements Iterable<@NonNull ApplicationEntity>
{
  @NonNull
  private final Map<@NonNull Class, Map<@NonNull Long, @NonNull ApplicationEntity>> _entityIndex;

  @NonNull
  private final Set<@NonNull ApplicationEntity> _entities;

  @NonNull
  private final ApplicationEntityIdentifiers _identifiers;

  public ApplicationEntityCollection () {
    _entities = new HashSet<>();
    _entityIndex = new HashMap<>();
    _identifiers = new ApplicationEntityIdentifiers();
  }

  public ApplicationEntityCollection (@NonNull final Iterable<ApplicationEntity> entities) {
    _entities = new HashSet<>();
    _entityIndex = new HashMap<>();
    _identifiers = new ApplicationEntityIdentifiers();

    entities.forEach(this::add);
  }

  public ApplicationEntityCollection (@NonNull final ApplicationEntityCollection toCopy) {
    _entities = new HashSet<>();
    _entityIndex = new HashMap<>();
    _identifiers = new ApplicationEntityIdentifiers(toCopy._identifiers);

    toCopy.forEach(this::add);
  }

  public void add (@NonNull final ApplicationEntity entity) {
    if (entity.getIdentifier() == null) entity.setIdentifier(_identifiers.next(entity));

    if (!_entities.contains(entity)) {
      @NonNull final ApplicationEntity clone = Duplicator.duplicate(entity);
      @NonNull Class                   type  = entity.getClass();

      while (!Objects.equals(type, ApplicationEntity.class)) {
        if (!_entityIndex.containsKey(type)) {
          _entityIndex.put(type, new HashMap<>());
        }

        _entityIndex.get(type).put(entity.getIdentifier(), clone);
        type = type.getSuperclass();
      }

      _entities.add(clone);
    }
  }

  public void remove (@NonNull final ApplicationEntity entity) {
    if (entity.getIdentifier() == null) return;

    if (_entities.contains(entity)) {
      @NonNull Class type = entity.getClass();
      _entities.remove(entity);

      while (!Objects.equals(type, ApplicationEntity.class)) {
        _entityIndex.get(type).remove(entity.getIdentifier());

        if (_entityIndex.get(type).size() <= 0) {
          _entityIndex.remove(type);
        }

        type = type.getSuperclass();
      }

      _entities.remove(entity);
      entity.setIdentifier(null);
    }
  }

  public void remove (
    @NonNull final Class<? extends ApplicationEntity> type, @NonNull final Long identifier
  )
  {
    if (contains(type, identifier)) remove(_entityIndex.get(type).get(identifier));
  }

  public <Entity extends ApplicationEntity> @NonNull List<@NonNull Entity> get (@NonNull final Class<Entity> type) {
    if (_entityIndex.containsKey(type)) {
      return _entityIndex.get(type)
                         .values()
                         .stream()
                         .map(entity -> Duplicator.duplicate(type.cast(entity)))
                         .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public @NonNull ApplicationEntity get (@NonNull final ApplicationEntity entity) {
    if (!contains(entity)) throw new EntityNotFoundException();

    return get(entity.getClass(), entity.getIdentifier());
  }

  public <Entity extends ApplicationEntity> @NonNull Entity get (
    @NonNull final Class<Entity> type, @NonNull final Long identifier
  )
  {
    return Duplicator.duplicate(type.cast(_entityIndex.get(type).get(identifier)));
  }

  public boolean contains (@NonNull final ApplicationEntity entity) {
    return entity.getIdentifier() != null && _entities.contains(entity);
  }

  public boolean contains (@NonNull final Class<? extends ApplicationEntity> type, @NonNull final Long value) {
    return _entityIndex.containsKey(type) && _entityIndex.get(type).containsKey(value);
  }

  public void clear (@NonNull final Class<? extends ApplicationEntity> type) {
    if (_entityIndex.containsKey(type)) {
      for (@NonNull final ApplicationEntity entity : new HashSet<>(_entityIndex.get(type).values())) {
        remove(entity);
      }
    }
  }

  public void clear () {
    for (@NonNull final ApplicationEntity entity : new HashSet<>(_entities)) {
      remove(entity);
    }
  }

  public @NonNegative int getSize () {
    return _entities.size();
  }

  public @NonNegative int getSize (@NonNull final Class<? extends ApplicationEntity> type) {
    if (_entityIndex.containsKey(type)) {
      return _entityIndex.get(type).size();
    }

    return 0;
  }

  public <Entity extends ApplicationEntity> @NonNull Iterator<@NonNull Entity> iterator (
    @NonNull final Class<Entity> type
  )
  {
    if (_entityIndex.containsKey(type)) {
      return _entityIndex.get(type).values().stream().map(entity -> Duplicator.duplicate(type.cast(entity))).iterator();
    } else {
      return Iterators.forArray();
    }
  }

  @Override
  public @NonNull Iterator<@NonNull ApplicationEntity> iterator () {
    return _entities.stream().map(Duplicator::duplicate).iterator();
  }
}
