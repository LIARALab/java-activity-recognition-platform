package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.repository.local.ApplicationEntityManager;
import org.liara.api.event.ApplicationEntityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

public class LocalApplicationEntityDriver
{
  @NonNull
  private final ApplicationEntityManager _manager;

  @Autowired
  public LocalApplicationEntityDriver (
    @NonNull final ApplicationEntityManager manager
  )
  { _manager = manager; }


  @EventListener
  public void create (final ApplicationEntityEvent.@NonNull Create creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) { _manager.merge(entity); }
  }

  @EventListener
  public void mutate (final ApplicationEntityEvent.@NonNull Update mutation) {
    for (@NonNull final ApplicationEntity entity : mutation.getEntities()) { _manager.merge(entity); }
  }

  @EventListener
  public void delete (final ApplicationEntityEvent.@NonNull Delete deletion) {
    for (@NonNull final ApplicationEntity entity : deletion.getEntities()) { _manager.remove(entity); }
  }
}
