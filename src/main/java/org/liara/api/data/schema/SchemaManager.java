package org.liara.api.data.schema;

import org.springframework.lang.NonNull;

public interface SchemaManager
{
  public <Entity> Entity execute (@NonNull final Object schema);
}
