package org.liara.api.data.repository.local;

import com.google.common.collect.Streams;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LocalApplicationEntityRepository<Entity extends ApplicationEntity> 
       extends BaseLocalRepository
       implements ApplicationEntityRepository<Entity>
{
  @NonNull
  final Class<Entity> _type;
  
  public static <StoredEntity extends ApplicationEntity> LocalApplicationEntityRepository<StoredEntity> from (
    @NonNull final LocalEntityManager parent,
    @NonNull final Class<StoredEntity> type
  ) {
    final LocalApplicationEntityRepository<StoredEntity> result = new LocalApplicationEntityRepository<>(type);
    result.setParent(parent);
    return result;
  }
  
  public LocalApplicationEntityRepository (
    @NonNull final Class<Entity> type
  ) {
    super();
    _type = type;
  }
  
  @Override
  public Optional<Entity> find (
    @NonNull final ApplicationEntityReference<Entity> reference
  ) {
    return find(reference.getIdentifier());
  }
  
  public Optional<Entity> find (@NonNull final Long identifier) {
    if (getParent() == null) return Optional.empty();
    return getParent().find(_type, identifier);
  }

  @Override
  public List<Entity> findAll () {
    if (getParent() == null) return Collections.emptyList();
    return getParent().findAll(_type);
  }

  public void addAll (@NonNull final Iterable<Entity> entities) {
    if (getParent() != null) {
      getParent().addAll(entities);
    }
  }
  
  public void addAll (@NonNull final Iterator<Entity> entities) {
    if (getParent() != null) {
      getParent().addAll(entities);
    }
  }
  
  public void removeAll (@NonNull final Iterable<Entity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public void removeAll (@NonNull final Iterator<Entity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public boolean contains (@NonNull final Entity entity) {
    if (getParent() == null) return false;
    return getParent().contains(_type, entity.getIdentifier());
  }
  
  public boolean contains (@NonNull final Long identifier) {
    if (getParent() == null) return false;
    return getParent().contains(_type, identifier);
  }
  
  public void clear () {
    getParent().clear(_type);
  }
  
  public int size () {
    return getParent().size(_type);
  }

  @Override
  protected void entityWasAdded (@NonNull final ApplicationEntity entity) {
    super.entityWasAdded(entity);
    
    if (_type.isAssignableFrom(entity.getClass()) && getParent() != null) {
      trackedEntityWasAdded(_type.cast(entity));
    }
  }
  
  protected void trackedEntityWasAdded (@NonNull final Entity entity) {
    
  }

  @Override
  protected void entityWasRemoved (@NonNull final ApplicationEntity entity) {
    super.entityWasRemoved(entity);
    
    if (_type.isAssignableFrom(entity.getClass()) && getParent() != null) {
      trackedEntityWasRemoved(_type.cast(entity));
    }
  }

  protected void trackedEntityWasRemoved (@NonNull final Entity entity) {
    
  }
}
