/**
 * Copyright 2018 Cédric DEMONGIVERT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.domus.api.filter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.domus.api.date.PartialLocalDateTime;
import org.springframework.lang.NonNull;

/**
 * Keep only values that are included in a given range.
 *  
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class TemporalEqualFilter implements Filter<LocalDateTime>
{
  @NonNull private PartialLocalDateTime _value;
  
  /**
   * Create a new equal filter.
   * 
   * @param value Value.
   */
  public TemporalEqualFilter (@NonNull final PartialLocalDateTime value) {
    this._value = value;
  }

  @Override
  public Predicate toPredicate (
    @NonNull final Path<LocalDateTime> path, 
    @NonNull final CriteriaQuery<?> value, 
    @NonNull final CriteriaBuilder builder
  ) {
    if (_value.isCompleteLocalDateTime()) {
      return builder.equal(path, _value.toLocalDateTime());
    } else {
      final List<Predicate> predicates = new ArrayList<>();
      
      if (_value.containsDatetime()) {
        predicates.add(
          builder.equal(
            _value.mask(path, builder),
            _value.toLocalDateTime()
          )
        );
      }
      
      if (_value.containsContext()) {
        Arrays.stream(PartialLocalDateTime.CONTEXT_FIELDS)
              .filter(_value::isSupported)
              .map(field -> builder.equal(
                 PartialLocalDateTime.select(path, builder, field), 
                 _value.getLong(field))
               )
              .forEach(predicates::add);
      }
      
      return builder.and(predicates.toArray(new Predicate[predicates.size()])); 
    }
  }
  
  /**
   * @see org.domus.api.filter.Filter#accept(java.lang.Object)
   */
  public boolean accept (@NonNull final LocalDateTime value) {
    return _value.compareTo(value) == 0; 
  }

  public PartialLocalDateTime getValue () {
    return _value;
  }
  
  public void setValue (@NonNull final PartialLocalDateTime value) {
    _value = value;
  }
}
