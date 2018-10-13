package org.liara.api.data.schema;

import org.liara.api.utils.Beans;
import org.springframework.lang.NonNull;

import java.util.Map;

public interface ApplicationSchema
{
  public default boolean lookLike (@NonNull final Map<String, ?> values) {
    return Beans.lookLike(this, values);
  }
}
