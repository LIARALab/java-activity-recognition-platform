package org.liara.api.data.repository.local;

import com.google.common.collect.Streams;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import java.util.*;

public class LocalEntityManager
{
  @NonNull
  private final Map<Class<? extends ApplicationEntity>, Map<Long, ApplicationEntity>> _entities = new HashMap<>();
  
  @NonNull
  private final Set<LocalEntityManagerListener> _listeners = Collections.newSetFromMap(
    new WeakHashMap<LocalEntityManagerListener, Boolean>()
  );
  
  @NonNull
  private final ApplicationEntityIdentifiers _identifiers = new ApplicationEntityIdentifiers();
  
  public <SearchedEntity extends ApplicationEntity> Optional<SearchedEntity> find (
    @NonNull final Class<SearchedEntity> type, 
    @NonNull final Long identifier
  ) {
    if (_entities.containsKey(type)) {
      if (_entities.get(type).containsKey(identifier)) {
        return Optional.ofNullable(
          type.cast(_entities.get(type).get(identifier))
        );
      }
    }
    
    return Optional.empty();
  }
  
  public <SearchedEntity extends ApplicationEntity> Optional<? extends SearchedEntity> find (
    @NonNull final ApplicationEntityReference<SearchedEntity> reference
  ) {
    return find(reference.getType(), reference.getIdentifier());
  }
  
  public <SearchedEntity extends ApplicationEntity> SearchedEntity getAt (
    @NonNull final ApplicationEntityReference<SearchedEntity> reference
  ) {
    return find(reference.getType(), reference.getIdentifier()).get();
  }
  
  public <SearchedEntity extends ApplicationEntity> List<SearchedEntity> findAll (
    @NonNull final Class<SearchedEntity> type
  ) {
    if (!_entities.containsKey(type)) return Collections.emptyList();
    
    final List<SearchedEntity> result = new ArrayList<>();
    _entities.get(type).values().forEach(x -> result.add(type.cast(x)));
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
      Class<? extends ApplicationEntity> type = entity.getClass();
      
      if (type.isAnnotationPresent(Entity.class)) {
        entity.setIdentifier(_identifiers.next(entity));
      }
      
      while (type.isAnnotationPresent(Entity.class)) {
        register(type, entity);
        type = (Class<? extends ApplicationEntity>) type.getSuperclass();
      }
      
      if (type != entity.getClass()) {
        for (final LocalEntityManagerListener listener : _listeners) {
          listener.add(entity);
        }
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public void merge (@NonNull final ApplicationEntity entity) {      
    Class<? extends ApplicationEntity> type = entity.getClass();
    
    while (type.isAnnotationPresent(Entity.class)) {
      register(type, entity);
      type = (Class<? extends ApplicationEntity>) type.getSuperclass();
    }
    
    if (type != entity.getClass()) {
      for (final LocalEntityManagerListener listener : _listeners) {
        listener.add(entity);
      }
    }
  }
  
  private void register (
    @NonNull final Class<? extends ApplicationEntity> type,
    @NonNull final ApplicationEntity entity
  ) {
    if (!_entities.containsKey(type)) {
      _entities.put(type, new HashMap<>());
    }
    
    _entities.get(type).put(entity.getIdentifier(), entity);
  }

  public void addAll (@NonNull final Iterable<? extends ApplicationEntity> entities) {
    Streams.stream(entities).forEach(this::add);
  }
  
  public void addAll (@NonNull final Iterator<? extends ApplicationEntity> entities) {
    Streams.stream(entities).forEach(this::add);
  }
  
  public void addListener (@NonNull final LocalEntityManagerListener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
      listener.setParent(this);
      
      _entities.values().stream().forEach(submap -> {
        submap.values().stream().forEach(listener::add);
      });
    }
  }
  
  public void removeListener (@NonNull final LocalEntityManagerListener listener) {
    if (_listeners.contains(listener)) {      
      _listeners.remove(listener);
      listener.setParent(null);
      
      _entities.values().stream().forEach(submap -> {
        submap.values().stream().forEach(listener::add);
      });
    }
  }
  
  @SuppressWarnings("unchecked")
  public void remove (@NonNull final ApplicationEntity entity) {    
    if (contains(entity)) {
      Class<? extends ApplicationEntity> type = entity.getClass();
      
      while (type.isAnnotationPresent(Entity.class)) {
        unregister(type, entity);
        type = (Class<? extends ApplicationEntity>) type.getSuperclass();
      }
      
      if (type != entity.getClass()) {
        for (final LocalEntityManagerListener listener : _listeners) {
          listener.remove(entity);
        }
      }
      
      entity.setIdentifier(null);
    }
  }
  
  private void unregister (
    @NonNull final Class<? extends ApplicationEntity> type, 
    @NonNull final ApplicationEntity entity
  ) {
    _entities.get(type).remove(entity.getIdentifier());
    
    if (_entities.get(type).size() <= 0) {
      _entities.remove(type);
    }
  }

  public void removeAll (@NonNull final Iterable<? extends ApplicationEntity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public void removeAll (@NonNull final Iterator<? extends ApplicationEntity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public boolean contains (@NonNull final ApplicationEntity entity) {
    return contains(entity.getClass(), entity.getIdentifier());
  }

  public boolean contains (
    @NonNull final Class<? extends ApplicationEntity> type, 
    @NonNull final Long identifier
  ) {
    return _entities.containsKey(type) 
        && _entities.get(type).containsKey(identifier);
  }
  
  public void clear () {
    final Iterator<Class<? extends ApplicationEntity>> types = _entities.keySet().iterator();
    
    while (types.hasNext()) {
      clear(types.next());
    }
    
    _entities.clear();
  }

  public void clear (@NonNull final Class<? extends ApplicationEntity> type) {
    while (_entities.containsKey(type) && !_entities.get(type).isEmpty()) {
      remove(_entities.get(type).entrySet().iterator().next().getValue());
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
    if (_entities.containsKey(type)) {
      return _entities.get(type).size();
    } else {
      return 0;
    }
  }
}
