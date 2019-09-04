package org.liara.api.data.graph;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.data.graph.Column;
import org.liara.data.graph.builder.ColumnBuilder;
import org.liara.data.graph.builder.GraphBuildingContext;
import org.liara.data.graph.builder.StaticColumnBuilder;
import org.liara.data.primitive.Primitive;

public final class ColumnDescription<Type>
  implements ColumnBuilder<Type>
{
  @NonNegative
  private final int _index;

  @NonNull
  private final String _name;

  @NonNull
  private final Primitive<Type> _type;

  @Nullable
  private Column<Type> _instance;

  public ColumnDescription (
    @NonNegative final int index,
    @NonNull final String name,
    @NonNull final Primitive<Type> type
  ) {
    _index = index;
    _name = name;
    _type = type;
    _instance = null;
  }

  @Override
  public @NonNull Column<Type> build (@NonNull final GraphBuildingContext graphBuildingContext) {
    if (_instance == null) {
      @NonNull final StaticColumnBuilder<Type> builder = new StaticColumnBuilder<>();
      builder.setType(_type);
      _instance = builder.build(graphBuildingContext);
    }

    return _instance;
  }

  public @Nullable Column<Type> get () {
    return _instance;
  }

  public @NonNull Column<Type> require () {
    if (_instance == null) {
      throw new IllegalStateException(
        "Unable to require the implementation of the described column \"" + _name + "\" of type" +
        _type.getName() + "because this column was not instantiated yet."
      );
    } else {
      return _instance;
    }
  }

  public @NonNegative int getIndex () {
    return _index;
  }

  public @NonNull String getName () {
    return _name;
  }

  public @NonNull Primitive<Type> getType () {
    return _type;
  }
}
