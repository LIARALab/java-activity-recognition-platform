package org.liara.api.relation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.collection.operator.Operator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class FactoryBasedRelation<Source, Destination>
  implements Relation<Source, Destination>
{
  @NonNull
  private final Class<Source> _sourceClass;

  @NonNull
  private final Class<Destination> _destinationClass;

  @NonNull
  private final Method _relativeFactory;

  @NonNull
  private final Method _absoluteFactory;

  public FactoryBasedRelation (
    @NonNull final FactoryBasedRelationBuilder<Source, Destination> builder
  ) {
    _sourceClass = Objects.requireNonNull(builder.getSourceClass());
    _destinationClass = Objects.requireNonNull(builder.getDestinationClass());
    _relativeFactory = Objects.requireNonNull(builder.getRelativeFactory());
    _absoluteFactory = Objects.requireNonNull(builder.getAbsoluteFactory());

    assertThatIsRelativeFactory(_sourceClass, _relativeFactory);
    assertThatIsAbsoluteFactory(_absoluteFactory);
  }

  public static void assertThatIsFactory (@NonNull final Method factory) {
    if (!Modifier.isStatic(factory.getModifiers())) {
      throw new Error(
        "The given method " + factory.getName() + " of class " +
        factory.getDeclaringClass().getName() + " is not a valid relation factory because " +
        "the given method was not static."
      );
    }

    if (!Modifier.isPublic(factory.getModifiers())) {
      throw new Error(
        "The given method " + factory.getName() + " of class " +
        factory.getDeclaringClass().getName() + " is not a valid relation factory because " +
        "the given method was not public."
      );
    }

    if (!Operator.class.isAssignableFrom(factory.getReturnType())) {
      throw new Error(
        "The given method " + factory.getName() + " of class " +
        factory.getDeclaringClass().getName() + " is not a valid relation factory because " +
        "the given method does not return an object assignable to " + Operator.class.toString() +
        "."
      );
    }
  }

  public static void assertThatIsAbsoluteFactory (@NonNull final Method factory) {
    assertThatIsFactory(factory);

    if (factory.getParameterCount() != 0) {
      throw new Error(
        "The given method " + factory.getName() + " of class " +
        factory.getDeclaringClass().getName() + " is not a valid absolute relation factory " +
        "because the given method does not take zero parameters."
      );
    }
  }

  public static void assertThatIsRelativeFactory (
    @NonNull final Class<?> sourceClass,
    @NonNull final Method factory
  ) {
    assertThatIsFactory(factory);

    if (factory.getParameterCount() != 1) {
      throw new Error(
        "The given method " + factory.getName() + " of class " +
        factory.getDeclaringClass().getName() + " is not a valid relative relation factory " +
        "because the given method does not take only one parameter."
      );
    }

    if (!sourceClass.isAssignableFrom(factory.getParameterTypes()[0])) {
      throw new Error(
        "The given method " + factory.getName() + " of class " +
        factory.getDeclaringClass().getName() + " is not a valid relative relation factory " +
        "because the given method does not take one parameter assignable from " +
        sourceClass.getName() + "."
      );
    }
  }

  @Override
  public @NonNull Operator getOperator () {
    try {
      return (Operator) _absoluteFactory.invoke(null);
    } catch (
        @NonNull final IllegalAccessException | @NonNull InvocationTargetException exception
    ) {
      throw new Error(
        "Unable to build a new absolute operator from the given factory method.",
        exception
      );
    }
  }

  @Override
  public @NonNull Operator getOperator (
    @NonNull final Source source
  ) {
    try {
      return (Operator) _relativeFactory.invoke(null, source);
    } catch (
        @NonNull final IllegalAccessException | @NonNull InvocationTargetException exception
    ) {
      throw new Error(
        "Unable to build a new absolute operator from the given factory method.",
        exception
      );
    }
  }

  @Override
  public @NonNull Class<Source> getSourceClass () {
    return _sourceClass;
  }

  @Override
  public @NonNull Class<Destination> getDestinationClass () {
    return _destinationClass;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof FactoryBasedRelation) {
      @NonNull final FactoryBasedRelation otherFactoryBasedRelation = (FactoryBasedRelation) other;

      return Objects.equals(_sourceClass, otherFactoryBasedRelation.getSourceClass()) &&
             Objects.equals(
               _destinationClass,
               otherFactoryBasedRelation.getDestinationClass()
             ) &&
             Objects.equals(_relativeFactory, otherFactoryBasedRelation._relativeFactory) &&
             Objects.equals(_absoluteFactory, otherFactoryBasedRelation._absoluteFactory);
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_sourceClass, _destinationClass, _relativeFactory, _absoluteFactory);
  }
}
