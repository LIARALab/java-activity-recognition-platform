package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.ApplicationEntityEvent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApplicationEntityInitializer
{
  @EventListener
  public void initialize (final ApplicationEntityEvent.@NonNull Initialize initialization) {
    @NonNull final ApplicationEntity applicationEntity = initialization.getApplicationEntity();

    applicationEntity.setCreationDate(ZonedDateTime.now());
    applicationEntity.setUpdateDate(ZonedDateTime.now());
    applicationEntity.setDeletionDate(null);
    if (applicationEntity.getUUID() == null) applicationEntity.setUUID(UUID.randomUUID());
  }
}
