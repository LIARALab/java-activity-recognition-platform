package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.primitive.Primitives;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SensorTable
  extends TableDescription
{
  @NonNull
  public final ColumnDescription createdAt = column(
    "created_at", Primitives.DATE_TIME
  );

  @NonNull
  public final ColumnDescription updatedAt = column(
    "updated_at", Primitives.DATE_TIME
  );

  @NonNull
  public final ColumnDescription deletedAt = column(
    "deleted_at", Primitives.NULLABLE_DATE_TIME
  );

  @NonNull
  public final ColumnDescription identifier = column(
    "identifier", Primitives.INTEGER
  );

  @NonNull
  public final ColumnDescription uuid = column(
    "uuid", Primitives.STRING
  );

  @NonNull
  public final ColumnDescription configuration = column(
    "configuration", Primitives.NULLABLE_STRING
  );

  @NonNull
  public final ColumnDescription name = column(
    "name", Primitives.STRING
  );

  @NonNull
  public final ColumnDescription nodeIdentifier = column(
    "node_identifier", Primitives.INTEGER
  );

  @NonNull
  public final ColumnDescription type = column(
    "type", Primitives.STRING
  );

  @Override
  public @NonNull String getName () {
    return "sensors";
  }
}
