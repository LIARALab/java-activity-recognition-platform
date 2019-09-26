package org.liara.api.event.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationEvent;

public abstract class ApplicationEntityEvent
  extends ApplicationEvent
{
  public ApplicationEntityEvent (@NonNull final Object source) {
    super(source);
  }
}
