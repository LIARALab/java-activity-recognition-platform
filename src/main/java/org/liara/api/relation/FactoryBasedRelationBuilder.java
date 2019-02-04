package org.liara.api.relation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

public class FactoryBasedRelationBuilder<Source, Destination>
{
  @Nullable
  private Class<Source> _sourceClass;

  @Nullable
  private Class<Destination> _destinationClass;

  @Nullable
  private Method _relativeFactory;

  @Nullable
  private Method _absoluteFactory;

  public FactoryBasedRelationBuilder () {
    _sourceClass = null;
    _destinationClass = null;
    _relativeFactory = null;
    _absoluteFactory = null;
  }

  public FactoryBasedRelationBuilder (
    @NonNull final FactoryBasedRelationBuilder<Source, Destination> builder
  ) {
    _sourceClass = builder.getSourceClass();
    _destinationClass = builder.getDestinationClass();
    _relativeFactory = builder.getRelativeFactory();
    _absoluteFactory = builder.getAbsoluteFactory();
  }

  public static <A, B> @NonNull FactoryBasedRelationBuilder<A, B> create (
    @NonNull final Class<A> sourceClass,
    @NonNull final Class<B> destinationClass
  ) {
    @NonNull final FactoryBasedRelationBuilder<A, B> builder = new FactoryBasedRelationBuilder<>();
    builder.setSourceClass(sourceClass);
    builder.setDestinationClass(destinationClass);
    return builder;
  }

  public @Nullable Class<Source> getSourceClass () {
    return _sourceClass;
  }

  public void setSourceClass (@Nullable final Class<Source> sourceClass) {
    _sourceClass = sourceClass;
  }

  public @Nullable Class<Destination> getDestinationClass () {
    return _destinationClass;
  }

  public void setDestinationClass (@Nullable final Class<Destination> destinationClass) {
    _destinationClass = destinationClass;
  }

  public @Nullable Method getRelativeFactory () {
    return _relativeFactory;
  }

  public void setRelativeFactory (@Nullable final Method relativeFactory) {
    _relativeFactory = relativeFactory;
  }

  public @Nullable Method getAbsoluteFactory () {
    return _absoluteFactory;
  }

  public void setAbsoluteFactory (@Nullable final Method absoluteFactory) {
    _absoluteFactory = absoluteFactory;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof FactoryBasedRelationBuilder) {
      @NonNull final FactoryBasedRelationBuilder otherFactoryBasedRelationBuilder =
        (FactoryBasedRelationBuilder) other;

      return
        Objects.equals(_sourceClass, otherFactoryBasedRelationBuilder.getSourceClass()) &&
        Objects.equals(
          _destinationClass,
          otherFactoryBasedRelationBuilder.getDestinationClass()
        ) &&
        Objects.equals(
          _relativeFactory,
          otherFactoryBasedRelationBuilder.getRelativeFactory()
        ) &&
        Objects.equals(
          _absoluteFactory,
          otherFactoryBasedRelationBuilder.getAbsoluteFactory()
        );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_sourceClass, _destinationClass, _relativeFactory, _absoluteFactory);
  }
}
