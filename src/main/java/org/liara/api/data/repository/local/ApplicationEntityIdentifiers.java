package org.liara.api.data.repository.local;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.lang.NonNull;

public class ApplicationEntityIdentifiers
{
  private Map<Class<? extends ApplicationEntity>, Long> _nexts = new HashMap<>();
  
  public Long next (@NonNull final Class<? extends ApplicationEntity> type) {
    final Class<? extends ApplicationEntity> realType = getTypeOf(type);
    if (!_nexts.containsKey(realType)) {
      _nexts.put(realType, 0L);
    }
    
    final Long result = _nexts.get(realType);
    _nexts.put(realType, result + 1);
    return result;
  }
  
  public Long next (@NonNull final ApplicationEntity entity) {
    return next(entity.getClass());
  }
  
  @SuppressWarnings("unchecked")
  protected Class<? extends ApplicationEntity> getTypeOf (@NonNull final Class<? extends ApplicationEntity> subType) {
    if (!subType.isAnnotationPresent(Entity.class)) return null;
    
    Class<? extends ApplicationEntity> result = subType;
    
    while (result.getSuperclass().isAnnotationPresent(Entity.class)) {
      result = (Class<? extends ApplicationEntity>) result.getSuperclass();
    }
    
    return result;
  }
}
