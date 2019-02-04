package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.Operator;

public class StaticComputedCollectionResource<Model extends ApplicationEntity>
  extends ComputedCollectionResource<Model>
{
  @NonNull
  private final Operator _operator;

  public StaticComputedCollectionResource (
    final @NonNull Class<Model> modelClass,
    final @NonNull Operator operator,
    final @NonNull CollectionResourceBuilder builder
  ) {
    super(modelClass, builder);
    _operator = operator;

    System.out.println("Created collection : " + ((JPAEntityCollection) getCollection()).getQuery(
      ":this"));
  }

  /**
   * Return the operator applied to the underlying collection.
   *
   * @return The operator applied to the underlying collection.
   */
  @Override
  public @NonNull Operator getOperator () {
    return _operator;
  }
}
