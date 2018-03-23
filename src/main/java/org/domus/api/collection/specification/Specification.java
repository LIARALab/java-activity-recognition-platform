package org.domus.api.collection.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.lang.NonNull;

/**
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Restricted entity type.
 */
public interface Specification<Entity>
{
  /**
   * Build a predicate for the given specification.
   * 
   * @param root The targeted root of the criteria query. It is the entity model
   *          to restrict.
   * @param value A query to restrict.
   * @param builder A criteria builder.
   * @return A predicate to add to the query.
   */
  public Predicate build (
    @NonNull final Root<Entity> root,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  );
}
