package org.liara.api.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.springframework.lang.NonNull;

public class CursorBasedIterator<Element> implements Iterator<Element>
{
  public static <Element> CursorBasedIterator<Element> apply (
    @NonNull final Cursor cursor,
    @NonNull final Iterator<Element> iterator
  ) {
    return new CursorBasedIterator<>(cursor, iterator);
  }
  
  @NonNull private final Iterator<Element> _iterator;
  @NonNull private final Cursor _cursor;
  private int _returned;
  
  private CursorBasedIterator(
    @NonNull final Cursor cursor,
    @NonNull final Iterator<Element> iterator
  ) {
    _iterator = iterator;
    _cursor = cursor;
    _returned = 0;
    applyOffset();
  }
  
  private void applyOffset () {
    int offset = _cursor.getOffset();
    while (offset > 0 && _iterator.hasNext()) {
      _iterator.next();
      offset -= 1;
    }
  }
  
  @Override
  public boolean hasNext () {
    return _iterator.hasNext() && _returned < _cursor.getLimit();
  }

  @Override
  public Element next () {
    if (hasNext()) {
      _returned += 1;
      return _iterator.next();
    }
    
    throw new NoSuchElementException();
  }

}
