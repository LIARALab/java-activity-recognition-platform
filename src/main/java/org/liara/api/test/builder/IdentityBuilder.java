package org.liara.api.test.builder;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class IdentityBuilder<Type> 
       implements Builder<IdentityBuilder<Type>, Type>
{
  @Nullable
  private Type _value = null;
  
  public static <Type> IdentityBuilder<Type> of (@NonNull final Type value) {
    return new IdentityBuilder<Type>().withValue(value);
  }
  
  public IdentityBuilder<Type> withValue (@Nullable final Type value) {
    _value = value;
    return self();
  }
  
  @Override
  public Type build () {
    return _value;
  }

  @Override
  public IdentityBuilder<Type> self () {
    return this;
  }
}
