package org.domus.api.collection;

import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

@Component
public class EntityCollections {
  @NonNull private final ApplicationContext _context;

  @Autowired
  public EntityCollections (
    @NonNull final ApplicationContext context
  ) {
    this._context = context;
  }

  public <T> EntityCollection<T> create (
    @NonNull final Class<T> entity
  ) {
    return new CompleteEntityCollection<>(entity, this._context);
  }
}
