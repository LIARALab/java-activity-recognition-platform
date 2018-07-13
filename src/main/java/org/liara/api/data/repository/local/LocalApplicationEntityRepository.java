package org.liara.api.data.repository.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.springframework.lang.NonNull;

import com.google.common.collect.Streams;

public class LocalApplicationEntityRepository<Entity extends ApplicationEntity> 
       implements ApplicationEntityRepository<Entity>
{
  @NonNull
  private final Map<Long, Entity> _repository = new HashMap<>();
  
  @Override
  public Optional<Entity> find (
    @NonNull final ApplicationEntityReference<Entity> reference
  ) {
    return find(reference.getIdentifier());
  }
  
  public Optional<Entity> find (
    @NonNull final Long identifier
  ) {
    if (_repository.containsKey(identifier)) {
      return Optional.ofNullable(_repository.get(identifier));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<Entity> findAll () {
    return new ArrayList<>(_repository.values());
  }

  public void add (@NonNull final Entity entity) {
    if (!_repository.containsKey(entity.getIdentifier())) {
      _repository.put(entity.getIdentifier(), entity);
    }
  }

  public void addAll (@NonNull final Iterable<Entity> entities) {
    Streams.stream(entities).forEach(this::add);
  }
  
  public void addAll (@NonNull final Iterator<Entity> entities) {
    Streams.stream(entities).forEach(this::add);
  }
  
  public void remove (@NonNull final Entity entity) {
    if (_repository.containsKey(entity.getIdentifier())) {
      _repository.remove(entity.getIdentifier());
    }
  }
  
  public void removeAll (@NonNull final Iterable<Entity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public void removeAll (@NonNull final Iterator<Entity> entities) {
    Streams.stream(entities).forEach(this::remove);
  }
  
  public boolean contains (@NonNull final Entity entity) {
    return _repository.containsKey(entity.getIdentifier());
  }
  
  public void clear () {
    _repository.clear();
  }
  
  public int size () {
    return _repository.size();
  }
}
