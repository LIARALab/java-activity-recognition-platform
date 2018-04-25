/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
