package org.liara.api.collection.view;

import org.springframework.lang.Nullable;

public class ValueView<T> implements View<T>
{
  public static <T> ValueView<T> of (@Nullable final T value) {
    return new ValueView<>(value);
  }
  
  @Nullable
  private final T _value;
  
  public ValueView (@Nullable final T value) {
    _value = value;
  }

  @Override
  public T get () {
    return _value;
  }
}
