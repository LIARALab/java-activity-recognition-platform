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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.lang.NonNull;

/**
 * An object that filter other objects by comparing their content.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Filtered> Filtered object type.
 */
public interface Filter<Filtered>
{
  /**
   * Build a predicate for the given filter.
   * 
   * @param path Path to the field to filter.
   * @param value A query to restrict.
   * @param builder A criteria builder.
   * 
   * @return A predicate to add to the query.
   */
  public Predicate toPredicate (
    @NonNull final Path<Filtered> path,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  );
  
  /**
   * Return true if the given filter accept the value.
   * 
   * @param value Value to filter.
   * 
   * @return True if the given filter accept the value.
   */
  public boolean accept (@NonNull final Filtered value);
  
  @SuppressWarnings("unchecked")
  public default boolean acceptObject (@NonNull final Object value) {
    return this.accept((Filtered) value);
  }
  
  public default boolean reject (@NonNull final Filtered value) {
    return !this.accept(value);
  }
  
  public default boolean rejectObject (@NonNull final Object value) {
    return !this.acceptObject(value);
  }
}
