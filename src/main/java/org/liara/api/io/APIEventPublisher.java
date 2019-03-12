package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.entity.CreateApplicationEntityEvent;
import org.liara.api.event.entity.DeleteApplicationEntityEvent;
import org.liara.api.event.entity.UpdateApplicationEntityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class APIEventPublisher
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @Autowired
  public APIEventPublisher (@NonNull final ApplicationEventPublisher eventPublisher) {
    _applicationEventPublisher = eventPublisher;
  }

  public void delete (@NonNull final ApplicationEntity... entities) {
    _applicationEventPublisher.publishEvent(
      new DeleteApplicationEntityEvent(this, entities)
    );
  }

  public void create (@NonNull final ApplicationEntity... entities) {
    _applicationEventPublisher.publishEvent(
      new CreateApplicationEntityEvent(this, entities)
    );
  }

  public void update (@NonNull final ApplicationEntity... entities) {
    _applicationEventPublisher.publishEvent(
      new UpdateApplicationEntityEvent(this, entities)
    );
  }

  public @NonNull ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof APIEventPublisher) {
      @NonNull final APIEventPublisher otherAPIEventPublisher = (APIEventPublisher) other;

      return Objects.equals(
        _applicationEventPublisher,
        otherAPIEventPublisher.getApplicationEventPublisher()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_applicationEventPublisher);
  }
}
