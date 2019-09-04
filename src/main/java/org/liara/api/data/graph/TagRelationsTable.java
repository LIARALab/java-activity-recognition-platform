package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.primitive.Primitives;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class TagRelationsTable
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
  public final ColumnDescription name = column(
    "name", Primitives.STRING
  );

  @Override
  public @NonNull String getName () {
    return "tag_relations";
  }
}
