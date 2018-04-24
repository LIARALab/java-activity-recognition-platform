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
