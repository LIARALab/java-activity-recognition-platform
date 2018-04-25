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
package org.liara.api.collection.view;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.springframework.lang.NonNull;

public class MapView implements View<List<Object[]>>
{
  public static MapView apply (@NonNull final View<List<Tuple>> view) {
    return new MapView(view);
  }
  
  @NonNull
  private final View<List<Tuple>> _tuples;
  
  public MapView (@NonNull final View<List<Tuple>> tuples) {
    _tuples = tuples;
  }

  @Override
  public List<Object[]> get () {
    final List<Tuple> tuples = _tuples.get();
    final List<Object[]> result = new ArrayList<>(tuples.size());
    
    for (final Tuple tuple : tuples) {
      final List<TupleElement<?>> elements = tuple.getElements();
      final List<Object> values = new ArrayList<>(elements.size());
      final List<Object> keys = new ArrayList<>(elements.size());
      
      for (final TupleElement<?> element : elements) {
        if (element.getAlias().startsWith("key_")) {
          keys.add(tuple.get(element));
        } else {
          values.add(tuple.get(element));
        }
      }
      
      final Object value;
      final Object key;
      
      if (values.size() <= 0) {
        value = null;
      } else if (values.size() == 1) {
        value = values.get(0);
      } else {
        value = values;
      }
      
      if (keys.size() <= 0) {
        key = null;
      } else if (keys.size() == 1) {
        key = keys.get(0);
      } else {
        key = keys;
      }
      
      result.add(new Object[] { key, value });
    }
    
    return result;
  }
}
