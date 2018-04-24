package org.liara.api.collection.transformation;

import org.liara.api.collection.cursor.Cursor;
import org.liara.api.collection.view.CursorView;
import org.liara.api.collection.view.CriteriaQueryBasedView;
import org.springframework.lang.NonNull;

public class      CursorTransformation<Entity> 
       implements Transformation<
                     CriteriaQueryBasedView<Entity, ?>, 
                     CursorView<Entity>
                   >
{  
  @NonNull
  private final Cursor _cursor;
  
  public CursorTransformation(
    @NonNull final Cursor cursor
  ) { _cursor = cursor; }

  @Override
  public CursorView<Entity> apply (
    @NonNull final CriteriaQueryBasedView<Entity, ?> view
  ) {
    return new CursorView<>(view, _cursor);
  }
}
