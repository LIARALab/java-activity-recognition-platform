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
 * Negate another filter.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class NotFilter<Filtered> implements ComposedFilter<Filtered>
{
  @NonNull private Filter<Filtered> _negated;
  
  /**
   * Create a new negation of another filter.
   * 
   * @param negated The negated filter.
   */
  public NotFilter (@NonNull final Filter<Filtered> negated) {
    this._negated = negated;
  }
  
  @Override
  public Predicate toPredicate (
    @NonNull final Path<Filtered> path, 
    @NonNull final CriteriaQuery<?> value, 
    @NonNull final CriteriaBuilder builder
  ) {
    return builder.not(this._negated.toPredicate(path, value, builder));
  }
  
  /**
   * @see org.domus.api.filter.Filter#accept(java.lang.Object)
   */
  public boolean accept (@NonNull final Filtered value) {
    return !this._negated.accept(value);
  }
  
  /**
   * Return the negated filter.
   * 
   * @return The negated filter.
   */
  public Filter<Filtered> getNegated () {
    return this._negated;
  }
  
  /**
   * Change the negated filter.
   * 
   * @param negated The new filter to negate.
   */
  public void setNegated (@NonNull final Filter<Filtered> negated) {
    this._negated = negated;
  }
}
