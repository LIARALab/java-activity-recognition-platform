package org.liara.api.data.entity;

import java.util.Map;

import org.liara.api.utils.Beans;
import org.springframework.lang.NonNull;

public interface ApplicationSchema
{
  public default boolean lookLike (@NonNull final Map<String, ?> values) {
    return Beans.lookLike(this, values);
  }
}
