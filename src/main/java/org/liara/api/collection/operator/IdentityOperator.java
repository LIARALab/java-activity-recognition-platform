package org.liara.api.collection.operator;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public class IdentityOperator<Entity> implements EntityCollectionOperator<Entity>
{
  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity> query) {  }
}
