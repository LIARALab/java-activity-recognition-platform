package org.liara.api.event.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;

import java.util.Arrays;

public class DidDeleteApplicationEntityEvent
  extends ApplicationEntityEvent
{
  @NonNull
  private final ApplicationEntity[] _entities;

  public DidDeleteApplicationEntityEvent (
    @NonNull final Object source,
    @NonNull final ApplicationEntity... entities
  ) {
    super(source);
    _entities = Arrays.copyOf(entities, entities.length);
  }

  public @NonNull ApplicationEntity[] getEntities () {
    return Arrays.copyOf(_entities, _entities.length);
  }
}
