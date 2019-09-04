package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.primitive.Primitives;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class StatesLabelTable
  extends TableDescription
{
  @NonNull
  public final ColumnDescription stateIdentifier = column(
    "state_identifier", Primitives.INTEGER
  );

  @NonNull
  public final ColumnDescription name = column(
    "name", Primitives.STRING
  );

  @NonNull
  public final ColumnDescription start = column(
    "start", Primitives.DATE_TIME
  );

  @NonNull
  public final ColumnDescription end = column(
    "end", Primitives.NULLABLE_DATE_TIME
  );

  @Override
  public @NonNull String getName () {
    return "states_label";
  }
}
