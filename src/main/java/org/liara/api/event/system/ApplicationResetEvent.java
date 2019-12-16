package org.liara.api.event.system;

import org.springframework.context.ApplicationEvent;

public class ApplicationResetEvent
  extends ApplicationEvent
{
  /**
   * Create a new ApplicationEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public ApplicationResetEvent (final Object source) {
    super(source);
  }
}
