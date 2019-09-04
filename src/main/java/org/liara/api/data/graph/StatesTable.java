package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.primitive.Primitives;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class StatesTable
  extends TableDescription
{
  @NonNull
  public final ColumnDescription created_at = column(
    "created_at", Primitives.DATE_TIME
  );

  @NonNull
  public final ColumnDescription updated_at = column(
    "updated_at", Primitives.DATE_TIME
  );

  @NonNull
  public final ColumnDescription deleted_at = column(
    "deleted_at", Primitives.NULLABLE_DATE_TIME
  );

  @NonNull
  public final ColumnDescription identifier = column(
    "identifier", Primitives.INTEGER
  );

  @NonNull
  public final ColumnDescription emitted_at = column(
    "emitted_at", Primitives.DATE_TIME
  );

  @NonNull
  public final ColumnDescription sensor_identifier = column(
    "sensor_identifier", Primitives.INTEGER
  );

  @Override
  public @NonNull String getName () {
    return "states";
  }
}
