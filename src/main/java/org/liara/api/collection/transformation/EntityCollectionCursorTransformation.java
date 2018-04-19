package org.liara.api.collection.transformation;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.liara.api.collection.cursor.Cursor;
import org.liara.api.collection.query.EntityCollectionQuery;
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
  public CriteriaQuery<Entity> transformCriteria (
    @NonNull final EntityCollectionQuery<Entity> collectionQuery, 
    @NonNull final CriteriaQuery<Entity> query
  ) {
    query.select(collectionQuery.getEntity());
    return query;
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
