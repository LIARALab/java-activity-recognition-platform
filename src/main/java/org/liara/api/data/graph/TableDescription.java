package org.liara.api.data.graph;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.data.graph.Table;
import org.liara.data.graph.builder.GraphBuildingContext;
import org.liara.data.graph.implementation.StaticTable;
import org.liara.data.primitive.Primitive;
import org.liara.support.BaseComparators;
import org.liara.support.ListIndex;
import org.liara.support.view.View;

public abstract class TableDescription
  implements TableSupplier
{
  @NonNull
  private final ListIndex<@NonNull String, @NonNull ColumnDescription> _columns;
  @NonNull
  private final View<@NonNull String>                                  _names;
  @NonNull
  private final View<@NonNull ColumnDescription>                       _descriptions;
  @NonNegative
  private       int                                                    _nextColumnIndex;
  @Nullable
  private       Table                                                  _table;

  public TableDescription () {
    _nextColumnIndex = 0;
    _columns = new ListIndex<>(BaseComparators.STRING_COMPARATOR);
    _names = View.readonly(String.class, _columns.getKeys());
    _descriptions = View.readonly(ColumnDescription.class, _columns.getValues());
  }

  protected <Type> @NonNegative ColumnDescription column (
    @NonNull final String name,
    @NonNull final Primitive<Type> type
  ) {
    @NonNull final ColumnDescription<Type> description = new ColumnDescription<Type>(
      _nextColumnIndex, name, type
    );

    if (_columns.containsKey(name)) {
      throw new Error(
        "Unable to declare a column \"" + name + "\" of type " + type.getName() + " in table " +
        getName() + " because another column was already declared with the same name into it."
      );
    }

    _columns.put(name, description);
    _nextColumnIndex += 1;

    return description;
  }

  public @Nullable Table get () {
    return _table;
  }

  public @NonNull Table require () {
    if (_table == null) {
      throw new Error(
        "Unable to retrieve the implementation of the table description \"" + getName() + "\" " +
        "because it was not instantiated yet."
      );
    } else {
      return _table;
    }
  }

  @Override
  public @NonNull Table build (@NonNull final GraphBuildingContext graphBuildingContext) {
    if (_table == null) {
      _table = new StaticTable(graphBuildingContext, this);
    }

    return _table;
  }

  @Override
  public @NonNull ColumnDescription<?> getColumn (@NonNull final String name) {
    return _columns.getValue(name);
  }

  @Override
  public boolean containsColumn (@NonNull final String name) {
    return _columns.containsKey(name);
  }

  @Override
  public @NonNull View<@NonNull String> getColumnNames () {
    return _names;
  }

  @Override
  public @NonNull View<@NonNull ColumnDescription> getColumns () {
    return _descriptions;
  }
}
