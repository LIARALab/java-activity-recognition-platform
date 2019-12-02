package org.liara.api.event.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationEvent;

public class DidRejectEvent
  extends ConsumptionEvent
{
  public DidRejectEvent (@NonNull final Object source, @NonNull final ApplicationEvent event) {
    super(source, event);
  }
}
