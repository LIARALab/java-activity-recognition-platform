package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.repository.local.ApplicationEntityManager;
import org.liara.api.event.ApplicationEntityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
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
    _manager.merge(creation.getApplicationEntity());
  }

  @EventListener
  public void mutate (final ApplicationEntityEvent.@NonNull Update mutation) {
    _manager.merge(mutation.getApplicationEntity());
  }

  @EventListener
  public void delete (final ApplicationEntityEvent.@NonNull Delete deletion) {
    _manager.remove(deletion.getApplicationEntity());
  }
}
