package org.liara.api.relation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.joining.Join;

public interface Relation<Source, Destination>
{
  @NonNull Operator getOperator ();

  @NonNull Operator getOperator (@NonNull final Source source);

  @NonNull Class<Source> getSourceClass ();

  @NonNull Class<Destination> getDestinationClass ();

  default boolean isCollectionRelation () {
    return !isModelRelation();
  }

  default boolean isModelRelation () {
    return getOperator() instanceof Join;
  }
}
