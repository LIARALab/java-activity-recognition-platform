package org.liara.api.collection.view.cursor;

import org.liara.api.collection.transformation.cursor.Cursor;
import org.springframework.lang.NonNull;

public abstract class AbstractCursorView<Result> implements CursorView<Result>
{
  @NonNull
  private final Cursor _cursor;
  
  public AbstractCursorView (
    @NonNull final Cursor cursor
  ) {
    _cursor = cursor;
  }
  
  public AbstractCursorView (
    @NonNull final CursorView<Result> view
  ) {
    _cursor = view.getCursor();
  }
  
  @Override
  public Cursor getCursor () {
    return _cursor;
  }
}
