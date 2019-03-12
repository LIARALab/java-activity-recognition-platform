package org.liara.api.event.node;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.schema.NodeSchema;
import org.liara.api.event.entity.ApplicationEntityEvent;

public class CreateNodeEvent
  extends ApplicationEntityEvent
{
  @NonNull
  private final NodeSchema _schema;

  public CreateNodeEvent (@NonNull final Object source, @NonNull final NodeSchema schema) {
    super(source);

    _schema = schema;
  }

  public @NonNull NodeSchema getSchema () {
    return _schema;
  }
}
