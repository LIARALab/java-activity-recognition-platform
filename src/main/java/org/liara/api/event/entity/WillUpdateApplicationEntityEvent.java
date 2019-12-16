package org.liara.api.event.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;

public class WillUpdateApplicationEntityEvent
  extends ApplicationEntityEvent
{
  @NonNull
  private final ApplicationEntity _entity;

  public WillUpdateApplicationEntityEvent (
    @NonNull final Object source,
    @NonNull final ApplicationEntity entity
  ) {
    super(source);
    _entity = entity;
  }

  public @NonNull String getEntitiesType () {
    return _entity.getClass().getSimpleName();
  }

  public @NonNull ApplicationEntity getEntity () {
    return _entity;
  }

  @Override
  public String toString () {
    return super.toString() + " [ " + _entity.toString() + "]";
  }
}
