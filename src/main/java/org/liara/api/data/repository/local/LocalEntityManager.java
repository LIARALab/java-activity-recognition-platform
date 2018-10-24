package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.EntityNotFoundException;
import java.util.*;

public class LocalEntityManager
{
  @NonNull
  private final Map<@NonNull Class<? extends ApplicationEntity>, @NonNull Map<@NonNull Long,
                                                                                 @NonNull ApplicationEntity>> _entities = new HashMap<>();

  @NonNull
  private final Set<@NonNull LocalEntityManagerListener> _listeners = Collections.newSetFromMap(new WeakHashMap<>());

  @NonNull
  private final ApplicationEntityIdentifiers _identifiers = new ApplicationEntityIdentifiers();
  
  public <SearchedEntity extends ApplicationEntity> Optional<SearchedEntity> find (
    @NonNull final Class<SearchedEntity> type, @Nullable final Long identifier
  ) {
    if (identifier == null) return Optional.empty();

    @NonNull final Class<? extends ApplicationEntity> baseType = ApplicationEntity.getBaseTypeOf(type);

    if (_entities.containsKey(baseType) && _entities.get(baseType).containsKey(identifier) &&
        type.isInstance(_entities.get(baseType).get(identifier))) {
      return Optional.of(type.cast(_entities.get(baseType).get(identifier)));
    }
    
    return Optional.empty();
  }
  
  public <SearchedEntity extends ApplicationEntity> Optional<? extends SearchedEntity> find (
    @NonNull final ApplicationEntityReference<SearchedEntity> reference
  ) {
    return find(reference.getType(), reference.getIdentifier());
  }

  public <SearchedEntity extends ApplicationEntity> @NonNull SearchedEntity getAt (
    @NonNull final ApplicationEntityReference<SearchedEntity> reference
  ) {
    @NonNull final Optional<? extends SearchedEntity> result = find(reference.getType(), reference.getIdentifier());

    if (result.isPresent()) {
      return result.get();
    } else {
      throw new EntityNotFoundException();
    }
  }
  
  public <SearchedEntity extends ApplicationEntity> List<SearchedEntity> findAll (
    @NonNull final Class<SearchedEntity> type
  ) {
    @NonNull final Class<? extends ApplicationEntity> baseType = ApplicationEntity.getBaseTypeOf(type);
    if (!_entities.containsKey(baseType)) return Collections.emptyList();
    
    final List<SearchedEntity> result = new ArrayList<>();
    _entities.get(baseType).values().stream().filter(type::isInstance).forEach(x -> result.add(type.cast(x)));
    return result;
  }
  
  public <SearchedEntity extends ApplicationEntity> List<SearchedEntity> getAt (
    @NonNull final Class<SearchedEntity> type
  ) {
    return findAll(type);
  }
 
  @SuppressWarnings("unchecked")
  public void add (@NonNull final ApplicationEntity entity) {
    if (!contains(entity)) {
      final Class<? extends ApplicationEntity> type = entity.getBaseClass();
      entity.setIdentifier(_identifiers.next(type));

      register(entity);

      for (final LocalEntityManagerListener listener : _listeners) {
        listener.add(entity);
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public void merge (@NonNull final ApplicationEntity entity) {
    register(entity);

    for (final LocalEntityManagerListener listener : _listeners) {
      listener.add(entity);
    }
  }

  private void register (@NonNull final ApplicationEntity entity) {
    @NonNull final Class<? extends ApplicationEntity> type = entity.getBaseClass();

    if (!_entities.containsKey(type)) {
      _entities.put(type, new HashMap<>());
    }
    
    _entities.get(type).put(entity.getIdentifier(), entity);
  }

  public void addAll (@NonNull final Iterable<? extends ApplicationEntity> entities) {
    entities.forEach(this::add);
  }
  
  public void addAll (@NonNull final Iterator<? extends ApplicationEntity> entities) {
    entities.forEachRemaining(this::add);
  }
  
  public void addListener (@NonNull final LocalEntityManagerListener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
      listener.setParent(this);

      _entities.values().forEach(submap -> {
        submap.values().forEach(listener::add);
      });
    }
  }
  
  public void removeListener (@NonNull final LocalEntityManagerListener listener) {
    if (_listeners.contains(listener)) {      
      _listeners.remove(listener);
      listener.setParent(null);

      _entities.values().forEach(submap -> {
        submap.values().forEach(listener::add);
      });
    }
  }
  
  @SuppressWarnings("unchecked")
  public void remove (@NonNull final ApplicationEntity entity) {    
    if (contains(entity)) {
      unregister(entity);

      for (final LocalEntityManagerListener listener : _listeners) {
        listener.remove(entity);
      }
      
      entity.setIdentifier(null);
    }
  }

  private void unregister (@NonNull final ApplicationEntity entity) {
    @NonNull final Class<? extends ApplicationEntity> type = entity.getBaseClass();

    _entities.get(type).remove(entity.getIdentifier());
    
    if (_entities.get(type).size() <= 0) {
      _entities.remove(type);
    }
  }

  public void removeAll (@NonNull final Iterable<? extends ApplicationEntity> entities) {
    entities.forEach(this::remove);
  }
  
  public void removeAll (@NonNull final Iterator<? extends ApplicationEntity> entities) {
    entities.forEachRemaining(this::remove);
  }
  
  public boolean contains (@NonNull final ApplicationEntity entity) {
    return contains(entity.getClass(), entity.getIdentifier());
  }

  public boolean contains (@NonNull final Class<? extends ApplicationEntity> type, @Nullable final Long identifier) {
    if (identifier == null) return false;

    @NonNull final Class<? extends ApplicationEntity> baseType = ApplicationEntity.getBaseTypeOf(type);

    return _entities.containsKey(baseType) && _entities.get(baseType).containsKey(identifier) &&
           type.isInstance(_entities.get(baseType).get(identifier));
  }

  public void clear () {
    _entities.keySet().iterator().forEachRemaining(this::clear);
    _entities.clear();
  }

  public void clear (@NonNull final Class<? extends ApplicationEntity> type) {
    @NonNull final Class<? extends ApplicationEntity> baseType = ApplicationEntity.getBaseTypeOf(type);

    if (_entities.containsKey(baseType)) {
      _entities.get(baseType).values().stream().filter(type::isInstance).forEach(this::remove);
    }
  }
  
  public int size () {
    int result = 0;
    
    for (final Map<Long, ? extends ApplicationEntity> entries : _entities.values()) {
      result += entries.size();
    }
    
    return result;
  }

  public int size (@NonNull final Class<? extends ApplicationEntity> type) {
    @NonNull final Class<? extends ApplicationEntity> baseType = ApplicationEntity.getBaseTypeOf(type);

    if (_entities.containsKey(baseType)) {
      return (int) _entities.get(type).values().stream().filter(type::isInstance).count();
    } else {
      return 0;
    }
  }
}
