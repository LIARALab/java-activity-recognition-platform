package org.liara.api.collection.operator;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.transformation.Transformation;
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
       extends   Transformation<
                   EntityCollection<Entity>, 
                   EntityCollection<Entity>
                 >
{
  /**
   * Apply the current operator to the given query.
   * 
   * @param query Query to update.
   */
  public void apply (
    @NonNull final EntityCollectionQuery<Entity, ?> query
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
    @NonNull final Root<Entity> entity
  ) {
    apply(EntityCollectionQuery.from(manager, query, entity));
  }
  
  /**
   * Apply the current operator to the given query.
   * 
   * @param manager Manage related to the query.
   * @param query Query to mutate.
   * @param entity Targeted entity.
   */
  default public <Output> void apply (
    @NonNull final EntityManager manager,
    @NonNull final Subquery<Output> query,
    @NonNull final Root<Entity> entity
  ) {
    apply(EntityCollectionQuery.from(manager, query, entity));
  }
  
  /**
   * Apply this operator to a collection.
   * 
   * @param collection Collection to mutate.
   */
  default EntityCollection<Entity> apply (
    @NonNull final EntityCollection<Entity> collection
  ) {
    return collection.apply(this);
  }
  
  /**
   * Create an operator that apply the given operator and then apply the current operator on a collection.
   * 
   * @param operator Operator to conjugate.
   * @return An operator that apply the given operator and then apply the current operator on a collection.
   */
  default EntityCollectionOperator<Entity> apply (
    @NonNull final EntityCollectionOperator<Entity> operator
  ) {
    return new EntityCollectionConjunctionOperator<>(Arrays.asList(
      this, operator
    ));
  }
}
