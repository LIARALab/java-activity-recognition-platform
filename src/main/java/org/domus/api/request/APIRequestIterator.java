package org.domus.api.request;

import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Iterator;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class APIRequestIterator implements Iterator<Map.Entry<String, String[]>> {
  @NonNull private final Iterator<String> _parameters;
  @NonNull private final APIRequest _request;
  @Nullable private String _current;

  /**
  * Create a new iterator for a given request.
  *
  * @param request An api request to iterate.
  */
  public APIRequestIterator (@NonNull final APIRequest request) {
    this._parameters = request.parameters().iterator();
    this._request = request;
    this._current = null;
  }

  /**
  * @see Iterator#hasNext
  */
  public boolean hasNext () {
    return this._parameters.hasNext();
  }

  /**
  * @see Iterator#next
  */
  public Map.Entry<String, String[]> next () {
    this._current = this._parameters.next();

    return new AbstractMap.SimpleEntry<>(
      this._current,
      this._request.get(this._current)
    );
  }

  /**
  * @see Iterator#remove
  */
  public void remove () {
    if (this._current == null) throw new NoSuchElementException();

    this._request.remove(this._current);
    this._current = null;
  }
}
