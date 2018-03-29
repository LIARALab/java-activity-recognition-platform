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
public class TemporalBetweenFilter implements Filter<LocalDateTime>
{
  @NonNull final private TemporalGreaterThanOrEqualToFilter _minimum;
  @NonNull final private TemporalLessThanOrEqualToFilter _maximum;
  
  /**
   * Create a new less than filter.
   * 
   * @param a First bound (included).
   * @param b Second bound (included).
   */
  public TemporalBetweenFilter (@NonNull final PartialLocalDateTime a, @NonNull final PartialLocalDateTime b) {
    if (a.compareTo(b) <= 0) {
      _maximum = new TemporalLessThanOrEqualToFilter(b);
      _minimum = new TemporalGreaterThanOrEqualToFilter(a);
    } else {
      _maximum = new TemporalLessThanOrEqualToFilter(a);
      _minimum = new TemporalGreaterThanOrEqualToFilter(b);
    }
  }

  @Override
  public Predicate toPredicate (
    @NonNull final Path<LocalDateTime> path, 
    @NonNull final CriteriaQuery<?> value, 
    @NonNull final CriteriaBuilder builder
  ) {
    return builder.and(
      _maximum.toPredicate(path, value, builder),
      _minimum.toPredicate(path, value, builder)
    );
  }
  
  /**
   * @see org.domus.api.filter.Filter#accept(java.lang.Object)
   */
  public boolean accept (@NonNull final LocalDateTime value) {
    return _maximum.accept(value) && _minimum.accept(value);
  }

  public PartialLocalDateTime getMaximum () {
    return _maximum.getMaximum();
  }
  
  public void setMaximum (@NonNull final PartialLocalDateTime maximum) {
    _maximum.setMaximum(maximum);
  }

  public PartialLocalDateTime getMinimum () {
    return _minimum.getMinimum();
  }
  
  public void setMinimum (@NonNull final PartialLocalDateTime minimum) {
    _minimum.setMinimum(minimum);
  }
  
  public void setBounds (@NonNull final PartialLocalDateTime a, @NonNull final PartialLocalDateTime b) {
    if (a.compareTo(b) <= 0) {
      _maximum.setMaximum(b);
      _minimum.setMinimum(a);
    } else {
      _maximum.setMaximum(a);
      _minimum.setMinimum(b);
    }
  }
}
