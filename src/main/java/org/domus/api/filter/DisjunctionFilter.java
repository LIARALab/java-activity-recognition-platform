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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * A disjunction of filters.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class DisjunctionFilter<Filtered> implements ComposedFilter<Filtered>
{
  @NonNull private final List<Filter<Filtered>> _filters;
  
  /**
   * Create a new disjunction of filters.
   */
  public DisjunctionFilter () {
    _filters = new ArrayList<>();
  }
  
  /**
   * Create a new disjunction of filters.
   * 
   * @param filters Filters to add to the disjunction.
   */
  public DisjunctionFilter (@NonNull final Iterable<Filter<Filtered>> filters) {
    _filters = new ArrayList<>();
    Iterables.addAll(_filters, filters);
  }
  
  /**
   * Create a new disjunction of filters.
   * 
   * @param filters Filters to add to the disjunction.
   */
  public DisjunctionFilter (@NonNull final Iterator<Filter<Filtered>> filters) {
    _filters = new ArrayList<>();
    Iterators.addAll(_filters, filters);
  }
  
  /**
   * Create a new disjunction of filters.
   * 
   * @param filters Filters to add to the disjunction.
   */
  public DisjunctionFilter (@NonNull final Filter<Filtered>... filters) {
    _filters = new ArrayList<>();
    _filters.addAll(Arrays.asList(filters));
  }
  
  @Override
  public Predicate toPredicate (
    @NonNull final Path<Filtered> path, 
    @NonNull final CriteriaQuery<?> value, 
    @NonNull final CriteriaBuilder builder
  ) {
    final Predicate[] predicates = _filters.stream()
                                                .map(filter -> filter.toPredicate(path, value, builder))
                                                 .toArray(size -> new Predicate[size]);

    if (predicates.length <= 0) return builder.conjunction();
    else return builder.or(predicates);
  }
  
  /**
   * @see org.domus.api.filter.Filter#accept(java.lang.Object)
   */
  public boolean accept (@NonNull final Filtered value) {
    if (_filters.size() <= 0) return true;
    
    for (final Filter<Filtered> filter : _filters) {
      if (filter.accept(value)) return true;
    }
    
    return false;
  }
  
  public List<Filter<Filtered>> getFilters () {
    return _filters;
  }
}
