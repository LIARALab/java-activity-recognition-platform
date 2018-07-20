package org.liara.api.utils;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.NonNull;

public final class Beans
{
  public static boolean lookLike ( 
    @NonNull final Object raw,
    @NonNull final Map<String, ?> values
  ) {
    final BeanWrapper bean = new BeanWrapperImpl(raw);
    
    for (final Map.Entry<String, ?> value : values.entrySet()) {
      if (
        !Objects.equals(
          bean.getPropertyValue(value.getKey()), 
          value.getValue()
        )
      ) {
        return false;
      }
    }
    
    return true;
  }
}
