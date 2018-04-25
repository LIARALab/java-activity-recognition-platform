/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.collection.transformation.operator;

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
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
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
