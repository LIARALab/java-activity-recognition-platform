package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

public class InstanceDescriptor<Type>
{
  @NonNull
  private final Class<Type> _type;

  @NonNull
  private final Object[] _parameters;

  public InstanceDescriptor (@NonNull final Class<Type> type) {
    _type = type;
    _parameters = new Object[0];
  }

  public InstanceDescriptor (@NonNull final Class<Type> type, @Nullable final Object... parameters) {
    _type = type;
    _parameters = Arrays.copyOf(parameters, parameters.length);
  }

  public InstanceDescriptor (@NonNull final InstanceDescriptor<Type> toCopy) {
    _type = toCopy._type;
    _parameters = Arrays.copyOf(toCopy._parameters, toCopy._parameters.length);
  }

  public @NonNull Type instantiate () {
    try {
      return getConstructor().newInstance(_parameters);
    } catch (@NonNull final Exception exception) {
      throw new Error("Unable to instantiate an object of type " + _type.toString() + " : " + exception.getMessage(),
                      exception
      );
    }
  }

  public @NonNull Constructor<Type> getConstructor () {
    for (@NonNull final Constructor<?> constructor : _type.getConstructors()) {
      if (isValidConstructor(constructor)) return (Constructor<Type>) constructor;
    }

    throw new Error("Unable to find a valid constructor for " + _type.toString() + " and parameters " +
                    Arrays.stream(_parameters)
                          .map(value -> value.getClass().toString())
                          .reduce((a, b) -> a + ", " + b));
  }

  private boolean isValidConstructor (@NonNull final Constructor<?> constructor) {
    @NonNull final Class<?>[] parametersTypes = constructor.getParameterTypes();

    if (parametersTypes.length != _parameters.length) return false;

    for (int index = 0; index < _parameters.length; ++index) {
      if (!parametersTypes[index].isAssignableFrom(_parameters[index].getClass())) {
        return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_type, _parameters);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof InstanceDescriptor) {
      @NonNull final InstanceDescriptor otherDescriptor = (InstanceDescriptor) other;

      return Objects.equals(_type, otherDescriptor._type) &&
             Objects.deepEquals(_parameters, otherDescriptor._parameters);
    }

    return false;
  }
}
