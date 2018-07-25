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
  
  public static <T> T instanciate (
    @NonNull final Class<T> type,
    @NonNull final Map<String, ?> values 
  ) {
    try {
      final T result = type.newInstance();
      
      final BeanWrapper bean = new BeanWrapperImpl(result);
      
      for (final Map.Entry<String, ?> value : values.entrySet()) {
        bean.setPropertyValue(value.getKey(), value.getValue());
      }
      
      return result;
    } catch (final Exception exception) {
      throw new Error(exception);
    }
  }
}
