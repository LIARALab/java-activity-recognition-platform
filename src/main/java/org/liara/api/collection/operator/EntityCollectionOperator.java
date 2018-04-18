package org.liara.api.collection.operator;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Subquery;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.CriteriaEntityCollectionQuery;
import org.liara.api.collection.query.CriteriaEntityCollectionSubquery;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

/**
 * An operator that can be applied to mutate a given entity collection query.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Entity targeted by this operation.
 */
@FunctionalInterface
public interface EntityCollectionOperator<Entity>
{
  /**
   * Apply the current operator to the given query.
   * 
   * @param query Query to update.
   */
  public void apply (
    @NonNull final EntityCollectionQuery<Entity> query
  );
  
  /**
   * Apply the current operator to the given query.
   * 
   * @param manager Manage related to the query.
   * @param query Query to mutate.
   * @param entity Targeted entity.
   */
  default public void apply (
    @NonNull final EntityManager manager,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final From<?, Entity> entity
  ) {
    apply(new CriteriaEntityCollectionQuery<>(manager, query, entity));
  }
  
  /**
   * Apply the current operator to the given query.
   * 
   * @param manager Manage related to the query.
   * @param query Query to mutate.
   * @param entity Targeted entity.
   */
  default public void apply (
    @NonNull final EntityManager manager,
    @NonNull final Subquery<?> query,
    @NonNull final From<?, Entity> entity
  ) {
    apply(new CriteriaEntityCollectionSubquery<>(manager, query, entity));
  }
  
  /**
   * Apply this operator to a collection.
   * 
   * @param collection Collection to mutate.
   */
  default public void apply (
    @NonNull final EntityCollection<Entity> collection
  ) {
    collection.apply(this);
  }
}
