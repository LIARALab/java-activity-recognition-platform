package org.liara.api.data.repository.local;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.google.common.collect.Streams;

public class LocalApplicationEntityRepository<Entity extends ApplicationEntity> 
       implements ApplicationEntityRepository<Entity>, LocalEntityManagerListener
{
  @NonNull
  private final Class<Entity> _type;
  
  @Nullable
  private LocalEntityManager _parent;
  
  public static <StoredEntity extends ApplicationEntity> LocalApplicationEntityRepository<StoredEntity> of (
    @NonNull final LocalEntityManager parent,
    @NonNull final Class<StoredEntity> type
  ) {
    return new LocalApplicationEntityRepository<>(parent, type);
  }
  
  public LocalApplicationEntityRepository (
    @NonNull final Class<Entity> type
  ) {
    _type = type;
    _parent = null;
  }
  
  public LocalApplicationEntityRepository(
    @NonNull final LocalEntityManager parent,
    @NonNull final Class<Entity> type
  ) {
    _type = type;
    _parent = parent;
    parent.addListener(this);
  }
  
  @Override
  public Optional<Entity> find (
    @NonNull final ApplicationEntityReference<Entity> reference
  ) {
    return find(reference.getIdentifier());
  }
  
  public Optional<Entity> find (@NonNull final Long identifier) {
    if (_parent == null) return Optional.empty();
    return _parent.find(_type, identifier);
  }

  @Override
  public List<Entity> findAll () {
    if (_parent == null) return Collections.emptyList();
    return _parent.findAll(_type);
  }

  public void addAll (@NonNull final Iterable<Entity> entities) {
    if (_parent != null) {
      _parent.addAll(entities);
    }
  }
  
  public void addAll (@NonNull final Iterator<Entity> entities) {
    if (_parent != null) {
      _parent.addAll(entities);
    }
  }
  
  public void removeAll (@NonNull final Iterable<Entity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public void removeAll (@NonNull final Iterator<Entity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public boolean contains (@NonNull final Entity entity) {
    if (_parent == null) return false;
    return _parent.contains(_type, entity.getIdentifier());
  }
  
  public boolean contains (@NonNull final Long identifier) {
    if (_parent == null) return false;
    return _parent.contains(_type, identifier);
  }
  
  public void clear () {
    _parent.clear(_type);
  }
  
  public int size () {
    return _parent.size(_type);
  }

  @Override
  public LocalEntityManager getParent () {
    return _parent;
  }

  @Override
  public void setParent (@Nullable final LocalEntityManager parent) {
    if (_parent != parent) {
      if (_parent != null) {
        final LocalEntityManager oldParent = _parent;
        _parent = null;
        oldParent.removeListener(this);
      }
      
      _parent = parent;
      
      if (_parent != null) {
        _parent.addListener(this);
      }
    }
  }

  @Override
  public void add (@NonNull final ApplicationEntity entity) {
    if (_type.isAssignableFrom(entity.getClass()) && _parent != null) {
      _parent.add(entity);
      entityWasAdded(_type.cast(entity));
    }
  }

  protected void entityWasAdded (@NonNull final Entity entity) {
    
  }
  
  @Override
  public void remove (@NonNull final ApplicationEntity entity) {
    if (_type.isAssignableFrom(entity.getClass()) && _parent != null) {
      _parent.remove(entity);
      entityWasRemoved(_type.cast(entity));
    }
  }

  protected void entityWasRemoved (@NonNull final Entity entity) {
    
  }
}
