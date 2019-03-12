package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.repository.local.ApplicationEntityManager;
import org.liara.api.event.entity.CreateApplicationEntityEvent;
import org.liara.api.event.entity.DeleteApplicationEntityEvent;
import org.liara.api.event.entity.UpdateApplicationEntityEvent;
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
  public void create (final CreateApplicationEntityEvent creation) {
    for (@NonNull final ApplicationEntity entity : creation.getEntities()) { _manager.merge(entity); }
  }

  @EventListener
  public void mutate (final UpdateApplicationEntityEvent mutation) {
    for (@NonNull final ApplicationEntity entity : mutation.getEntities()) { _manager.merge(entity); }
  }

  @EventListener
  public void delete (final DeleteApplicationEntityEvent deletion) {
    for (@NonNull final ApplicationEntity entity : deletion.getEntities()) { _manager.remove(entity); }
  }
}
