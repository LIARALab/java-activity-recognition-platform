package org.liara.api.collection.transformation.cursor;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.view.EntityCollectionGrouping;
import org.liara.api.collection.view.cursor.CursorView;
import org.liara.api.collection.view.cursor.TypedQueryCursorView;
import org.springframework.lang.NonNull;

public class CursorTransformation
{
  @NonNull
  private final Cursor _cursor;
  
  public static CursorTransformation from (@NonNull final Cursor cursor) {
    return new CursorTransformation(cursor);
  }
  
  public CursorTransformation (@NonNull final Cursor cursor) {
    _cursor = cursor;
  }
  
  private <Result> TypedQuery<Result> produceFrom (@NonNull final EntityCollection<Result> collection) {
    final EntityCollectionMainQuery<Result, Result> query = collection.createCollectionQuery();
    query.getCriteriaQuery().select(query.getEntity());
    
    return query.getManager().createQuery(query.getCriteriaQuery());
  }

  public <Result> CursorView<Result> apply (
    @NonNull final EntityCollection<Result> collection
  ) {
    return new TypedQueryCursorView<>(() -> produceFrom(collection), _cursor);
  }
  
  private TypedQuery<Tuple> produceFrom (@NonNull final EntityCollectionGrouping<?> collection) {
    final EntityCollectionMainQuery<?, Tuple> query = collection.createQuery();
    return query.getManager().createQuery(query.getCriteriaQuery());
  }
  
  public CursorView<Tuple> apply (
    @NonNull final EntityCollectionGrouping<?> collection
  ) {
    return new TypedQueryCursorView<>(() -> produceFrom(collection), _cursor);
  }
}
