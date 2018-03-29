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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.lang.NonNull;

/**
 * Keep only values that are included in a given range.
 *  
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class BetweenFilter<Filtered extends Comparable<? super Filtered>> implements Filter<Filtered>
{
  @NonNull private Filtered _maximum;
  @NonNull private Filtered _minimum;
  
  /**
   * Create a new less than filter.
   * 
   * @param a First bound (included).
   * @param b Second bound (included).
   */
  public BetweenFilter (@NonNull final Filtered a, @NonNull final Filtered b) {
    if (a.compareTo(b) <= 0) {
      _maximum = b;
      _minimum = a;
    } else {
      _maximum = a;
      _minimum = b;
    }
  }

  @Override
  public Predicate toPredicate (
    @NonNull final Path<Filtered> path, 
    @NonNull final CriteriaQuery<?> value, 
    @NonNull final CriteriaBuilder builder
  ) {
    return builder.between(path, _minimum, _maximum);
  }
  
  /**
   * @see org.domus.api.filter.Filter#accept(java.lang.Object)
   */
  public boolean accept (@NonNull final Filtered value) {
    return value.compareTo(_maximum) <= 0 && value.compareTo(_minimum) >= 0; 
  }

  public Filtered getMaximum () {
    return _maximum;
  }
  
  public void setMaximum (@NonNull final Filtered maximum) {
    _maximum = maximum;
  }

  public Filtered getMinimum () {
    return _minimum;
  }
  
  public void setMinimum (@NonNull final Filtered minimum) {
    _minimum = minimum;
  }
  
  public void setBounds (@NonNull final Filtered a, @NonNull final Filtered b) {
    if (a.compareTo(b) <= 0) {
      _maximum = b;
      _minimum = a;
    } else {
      _maximum = a;
      _minimum = b;
    }
  }
}
