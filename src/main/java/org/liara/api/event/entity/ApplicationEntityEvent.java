package org.liara.api.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationEvent;

@JsonIgnoreProperties({"source", "unnamedModule", "classLoader"})
public abstract class ApplicationEntityEvent
  extends ApplicationEvent
{
  public ApplicationEntityEvent (@NonNull final Object source) {
    super(source);
  }

  public @NonNull String getType () {
    return getClass().getSimpleName();
  }
}
