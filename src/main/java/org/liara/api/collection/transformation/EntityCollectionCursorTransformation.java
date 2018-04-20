package org.liara.api.collection.transformation;

import javax.persistence.TypedQuery;

import org.liara.api.collection.cursor.Cursor;
import org.springframework.lang.NonNull;

public class EntityCollectionCursorTransformation<Entity> implements EntityCollectionTransformation<Entity, Entity>
{  
  @NonNull
  private final Cursor _cursor;
  
  public EntityCollectionCursorTransformation(
    @NonNull final Cursor cursor
  ) {
    _cursor = cursor;
  }

  @Override
  public TypedQuery<Entity> transformQuery (@NonNull final TypedQuery<Entity> query) {
    if (_cursor.hasLimit()) {
      query.setMaxResults(_cursor.getLimit());
    }
    
    query.setFirstResult(_cursor.getOffset());
    
    return query;
  }
}
