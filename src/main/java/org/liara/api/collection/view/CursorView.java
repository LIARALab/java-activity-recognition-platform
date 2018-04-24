package org.liara.api.collection.view;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.liara.api.collection.cursor.Cursor;
import org.springframework.lang.NonNull;

public class      CursorView<Result>
       implements CriteriaQueryBasedView<Result, List<Result>>
{
  @NonNull
  private final Cursor _cursor;
  
  @NonNull
  private final CriteriaQueryBasedView<Result, ?> _parentView;
  
  public CursorView(
    @NonNull final CriteriaQueryBasedView<Result, ?> parentView,
    @NonNull final Cursor cursor
  ) {
    _parentView = parentView;
    _cursor = cursor;
  }

  @Override
  public TypedQuery<Result> createTypedQuery () {
    final TypedQuery<Result> result = _parentView.createTypedQuery();
    
    if (_cursor.hasLimit()) {
      result.setMaxResults(_cursor.getLimit());
    }
    
    result.setFirstResult(_cursor.getOffset());
    
    return result;
  }

  @Override
  public <NextResult> CriteriaQuery<NextResult> createCriteriaQuery (@NonNull final Class<NextResult> result) {
    return _parentView.createCriteriaQuery(result);
  }

  @Override
  public List<Result> get () {
    return createTypedQuery().getResultList();
  }

  @Override
  public CriteriaQuery<Result> createCriteriaQuery () {
    return _parentView.createCriteriaQuery();
  }

  @Override
  public Class<Result> getQueryResultType () {
    return _parentView.getQueryResultType();
  }

  @Override
  public EntityManager getManager () {
    return _parentView.getManager();
  }
  
  public Cursor getCursor () {
    return _cursor;
  }
  
  public CursorView<Result> setCursor (
    @NonNull final Cursor cursor
  ) { return new CursorView<>(_parentView, cursor); }
  
  public CriteriaQueryBasedView<Result, ?> getParentView () {
    return _parentView;
  }
  
  public CriteriaQueryBasedView<Result, ?> setParentView (
    @NonNull final CriteriaQueryBasedView<Result, ?> parentView
  ) { return new CursorView<>(parentView, _cursor); }
}
