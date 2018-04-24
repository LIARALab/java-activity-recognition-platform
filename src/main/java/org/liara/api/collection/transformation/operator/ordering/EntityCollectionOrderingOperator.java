package org.liara.api.collection.transformation.operator.ordering;

import org.liara.api.collection.transformation.operator.EntityCollectionOperator;

public interface EntityCollectionOrderingOperator<Entity> extends EntityCollectionOperator<Entity>
{ 
  public static enum Direction
  {
    ASC, DESC;
  }
}
