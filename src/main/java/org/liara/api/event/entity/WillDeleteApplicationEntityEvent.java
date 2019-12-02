package org.liara.api.event.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class WillDeleteApplicationEntityEvent
  extends ApplicationEntityEvent
{
  @NonNull
  private final ApplicationEntity[] _entities;

  public WillDeleteApplicationEntityEvent (
    @NonNull final Object source,
    @NonNull final ApplicationEntity... entities
  ) {
    super(source);
    _entities = Arrays.copyOf(entities, entities.length);
  }

  public @NonNull ApplicationEntity[] getEntities () {
    return Arrays.copyOf(_entities, _entities.length);
  }

  public @NonNull String[] getEntitiesTypes () {
    return Arrays.stream(_entities)
                 .map(Object::getClass)
                 .map(Class::getSimpleName)
                 .toArray(String[]::new);
  }

  @Override
  public String toString () {
    return super.toString() + " [ " +
           Arrays.stream(_entities)
                 .map(Objects::toString)
                 .collect(Collectors.joining(", ")) +
           "]";
  }
}
