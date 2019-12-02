package org.liara.api.event.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationEvent;

public class DidConsumeEvent
  extends ConsumptionEvent
{
  public DidConsumeEvent (@NonNull final Object source, @NonNull final ApplicationEvent event) {
    super(source, event);
  }
}
