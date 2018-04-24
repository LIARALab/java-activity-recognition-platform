package org.liara.api.collection.view.cursor;

import java.util.List;

import org.liara.api.collection.transformation.cursor.Cursor;
import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

public interface CursorView<Result> extends View<List<Result>>
{
  public Cursor getCursor ();
  
  public CursorView<Result> setCursor (@NonNull final Cursor cursor);
}
