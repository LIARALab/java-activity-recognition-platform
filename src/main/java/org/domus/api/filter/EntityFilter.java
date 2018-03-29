/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.domus.api.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.NonNull;

public class EntityFilter<Filtered> implements ComposedFilter<Filtered>
{
  @NonNull private Map<String, Filter<?>> _fields;
  
  public EntityFilter () {
    _fields = new HashMap<>();
  }
  
  @Override
  public Predicate toPredicate (
    @NonNull final Path<Filtered> path, 
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  ) {
    final List<Predicate> predicates = new ArrayList<>();
    
    for (final Map.Entry<String, Filter<?>> field : this._fields.entrySet()) {
      predicates.add(
        field.getValue().toPredicate(
          path.get(field.getKey()), value, builder
        )
      );
    }
    
    return builder.and(predicates.toArray(new Predicate [predicates.size()]));
  }
  
  public boolean accept (@NonNull final Filtered filtered) {
    final BeanWrapper wrapper = new BeanWrapperImpl(filtered);
    
    for (final Map.Entry<String, Filter<?>> field : this._fields.entrySet()) {
      final Object value = wrapper.getPropertyValue(field.getKey());
      
      if (field.getValue().rejectObject(value)) {
        return false;
      }
    }
    
    return true;
  }
  
  public Map<String, Filter<?>> getFieldsFilter () {
    return _fields;
  }
}
