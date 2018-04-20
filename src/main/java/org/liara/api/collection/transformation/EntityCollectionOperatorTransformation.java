package org.liara.api.collection.transformation;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.springframework.lang.NonNull;

public class EntityCollectionOperatorTransformation<Input, Output>
       implements EntityCollectionTransformation<Input, Output>
{
  @NonNull
  private final EntityCollectionOperator<Input> _operator;
  
  public EntityCollectionOperatorTransformation(
    @NonNull final EntityCollectionOperator<Input> operator
  ) {
    _operator = operator;
  }

  @Override
  public EntityCollection<Input> transformCollection (@NonNull final EntityCollection<Input> collection) {
    return _operator.apply(collection);
  }
}
