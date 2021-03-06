package org.liara.api.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.event.entity.*;
import org.liara.api.event.event.DidConsumeEvent;
import org.liara.api.event.event.DidRejectEvent;
import org.liara.api.event.event.WillConsumeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
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

  public void willDelete (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new WillDeleteApplicationEntityEvent(this, entity)
    );
  }

  public void didDelete (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new DidDeleteApplicationEntityEvent(this, entity)
    );
  }

  public void create (@NonNull final ApplicationEntity... entities) {
    _applicationEventPublisher.publishEvent(
      new CreateApplicationEntityEvent(this, entities)
    );
  }

  public void initialize (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new InitializeApplicationEntityEvent(this, entity)
    );
  }

  public void willCreate (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new WillCreateApplicationEntityEvent(this, entity)
    );
  }

  public void didCreate (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new DidCreateApplicationEntityEvent(this, entity)
    );
  }

  public void update (@NonNull final ApplicationEntity... entities) {
    _applicationEventPublisher.publishEvent(
      new UpdateApplicationEntityEvent(this, entities)
    );
  }

  public void willUpdate (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new WillUpdateApplicationEntityEvent(this, entity)
    );
  }

  public void didUpdate (@NonNull final ApplicationEntity entity) {
    _applicationEventPublisher.publishEvent(
      new DidUpdateApplicationEntityEvent(this, entity)
    );
  }

  public void willConsume (@NonNull final ApplicationEvent event) {
    _applicationEventPublisher.publishEvent(new WillConsumeEvent(this, event));
  }

  public void didConsume (@NonNull final ApplicationEvent event) {
    _applicationEventPublisher.publishEvent(new DidConsumeEvent(this, event));
  }

  public void didReject (@NonNull final ApplicationEvent event) {
    _applicationEventPublisher.publishEvent(new DidRejectEvent(this, event));
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
