package org.liara.api.collection.operator;

public interface EntityCollectionOrderingOperator<Entity> extends EntityCollectionOperator<Entity>
{ 
  public static enum Direction
  {
    ASC, DESC;
  }
}
