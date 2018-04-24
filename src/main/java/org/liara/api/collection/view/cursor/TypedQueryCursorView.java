package org.liara.api.collection.view.cursor;

import java.util.List;

import javax.persistence.TypedQuery;

import org.liara.api.collection.transformation.cursor.Cursor;
import org.springframework.lang.NonNull;

public class   TypedQueryCursorView<Result> 
       extends AbstractCursorView<Result>
{
  @FunctionalInterface
  public static interface TypedQueryProducer<Result> {
    public TypedQuery<Result> produce ();
  }
  
  @NonNull
  private final TypedQueryProducer<Result> _producer;
  
  public TypedQueryCursorView (
    @NonNull final TypedQueryProducer<Result> producer, 
    @NonNull final Cursor cursor
  ) {
    super (cursor);
    _producer = producer;
  }

  @Override
  public List<Result> get () {
    final TypedQuery<Result> query = _producer.produce();
    
    if (getCursor().hasLimit()) {
      query.setMaxResults(getCursor().getLimit());
    } 
      
    query.setFirstResult(getCursor().getOffset());
    
    return query.getResultList();
  }
  
  public TypedQueryCursorView<Result> setCursor (@NonNull final Cursor cursor) {
    return new TypedQueryCursorView<>(_producer, cursor);
  }
}
