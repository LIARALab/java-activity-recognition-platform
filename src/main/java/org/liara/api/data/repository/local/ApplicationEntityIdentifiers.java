package org.liara.api.data.repository.local;

import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ApplicationEntityIdentifiers
{
  private Map<Class<? extends ApplicationEntity>, Long> _nexts;

  public ApplicationEntityIdentifiers () {
    _nexts = new HashMap<>();
  }

  public ApplicationEntityIdentifiers (@NonNull final ApplicationEntityIdentifiers toCopy) {
    _nexts = new HashMap<>(toCopy._nexts);
  }
  
  public Long next (@NonNull final Class<? extends ApplicationEntity> type) {
    final Class<? extends ApplicationEntity> realType = ApplicationEntity.getBaseTypeOf(type);

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
}
