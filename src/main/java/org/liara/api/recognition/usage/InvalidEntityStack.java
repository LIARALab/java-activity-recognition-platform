package org.liara.api.recognition.usage;

import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.lang.NonNull;

public class InvalidEntityStack<Entity> implements Iterator<Entity>
{
  @NonNull
  private final List<Entity> _entities;
  
  private int _index = 0;
  
  public InvalidEntityStack (@NonNull final TypedQuery<Entity> query) {
    _entities = query.getResultList();
  }

  @Override
  public boolean hasNext () {
    return _index < _entities.size();
  }

  @Override
  public Entity next () {
    final Entity result = _entities.get(_index);
    _index += 1;
    return result;
  }
}
