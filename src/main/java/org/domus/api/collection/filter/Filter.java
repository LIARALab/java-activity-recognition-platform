package org.domus.api.collection.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.lang.NonNull;

/**
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * A filter that mutate a criteria query in order to restrict
 * its selection to a subset of an entity collection.
 *
 * @param <T> Filtered entity.
 */
public interface Filter<T> {
  /**
   * Filter the given criteria query.
   * 
   * @param root The restricted query root.
   * @param value A query to restrict.
   * @param builder A criteria builder.
   */
  public void filter (
      @NonNull final Root<T> root,
      @NonNull final CriteriaQuery<T> value, 
      @NonNull final CriteriaBuilder builder
  );
}