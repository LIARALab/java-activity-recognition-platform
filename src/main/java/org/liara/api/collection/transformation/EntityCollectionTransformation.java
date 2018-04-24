package org.liara.api.collection.transformation;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.view.EntityCollectionView;
import org.springframework.lang.NonNull;

public interface EntityCollectionTransformation<Entity>
       extends   Transformation<
                   EntityCollectionView<Entity, ?>,
                   EntityCollectionView<Entity, ?>
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
}
