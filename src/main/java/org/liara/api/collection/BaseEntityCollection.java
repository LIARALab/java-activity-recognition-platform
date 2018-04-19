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
package org.liara.api.collection;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;

import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.collection.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.query.CriteriaEntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.transformation.EntityCollectionTransformation;
import org.liara.api.collection.view.BaseEntityCollectionView;
import org.liara.api.collection.view.EntityCollectionView;
import org.springframework.lang.NonNull;

/**
 * A sorted and filtered entity collection.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * @param <Entity> Type of entity in the collection.
 * @param <Identifier> Identifier type used for indexing the given entity type.
 */
public class BaseEntityCollection<Entity> 
       extends BaseEntityCollectionView<Entity>
       implements EntityCollection<Entity>
{
  /**
   * Operator to apply to the collection in order to get the selected results.
   */
  @NonNull
  private final EntityCollectionConjunctionOperator<Entity> _operator;

  /**
   * Create a collection for a given type and a given manager.
   * 
   * @param entity Entity type to store.
   * @param entityManager Entity Manager that manage the given type.
   */
  public BaseEntityCollection (
    @NonNull final Class<Entity> entity,
    @NonNull final EntityManager entityManager
  ) {
    super (entityManager, entity);
    _operator = new EntityCollectionConjunctionOperator<>();
  }
  
  /**
   * Create a copy of another collection.
   * 
   * @param collection Collection to copy.
   */
  public BaseEntityCollection (
    @NonNull final EntityCollection<Entity> collection
  ) {
    super (collection.getManager(), collection.getEntityType());
    _operator = new EntityCollectionConjunctionOperator<>(collection.getOperator());
  }
  
  /**
   * Create a copy of another collection and change the collection operator.
   * 
   * @param collection Collection to copy.
   * @param operator New operator to apply.
   */
  public BaseEntityCollection (
    @NonNull final EntityCollection<Entity> collection,
    @NonNull final EntityCollectionConjunctionOperator<Entity> operator
  ) {
    super (collection.getManager(), collection.getEntityType());
    _operator = operator;
  }

  /**
   * Create and return a collection query that select all entities of this collection and 
   * return a result of a given type.
   * 
   * @param result The result type of the query.
   * 
   * @return A collection query that select all entities of this collection and return a result of a given type.
   */
  public <Result> CriteriaEntityCollectionQuery<Entity, Result> createCollectionQuery (
    @NonNull final Class<Result> result
  ) {
    final CriteriaQuery<Result> query = getManager().getCriteriaBuilder().createQuery(result);
    final CriteriaEntityCollectionQuery<Entity, Result> collectionQuery = EntityCollectionQuery.from(
      getManager(), query, query.from(getEntityType())
    );
    
    _operator.apply(collectionQuery);
    
    return collectionQuery;
  }
  
  /**
   * Create an return a collection query that select all entities of this collection and return them.
   * 
   * @return A collection query that select all entities of this collection and return them.
   */
  public CriteriaEntityCollectionQuery<Entity, Entity> createCollectionQuery () {
    final CriteriaEntityCollectionQuery<Entity, Entity> collectionQuery = createCollectionQuery(getEntityType());
    
    collectionQuery.getCriteriaQuery().select(collectionQuery.getEntity());
    
    return collectionQuery;
  }
  
  @Override
  public CriteriaQuery<Entity> createQuery () {
    return createCollectionQuery().getCriteriaQuery();
  }

  @Override
  public <Identifier> Optional<Entity> findByIdentifier (@NonNull final Identifier identifier) {
    final CriteriaEntityCollectionQuery<Entity, Entity> query = createCollectionQuery();
    final EntityType<Entity> entityType = getManager().getMetamodel().entity(getEntityType());
    
    query.andWhere(getManager().getCriteriaBuilder().equal(
      query.getEntity().get(entityType.getId(identifier.getClass())), 
      identifier
    ));
    
    final List<Entity> resultList = getManager().createQuery(query.getCriteriaQuery())
                                                .getResultList();
    
    if (resultList.size() <= 0) {
      return Optional.empty();
    } else {
      return Optional.of(resultList.get(0));
    }
  }

  @Override
  public <Identifier> Entity findByIdentifierOrFail (@NonNull final Identifier identifier) throws EntityNotFoundException {
    final Optional<Entity> entity = findByIdentifier(identifier);
    
    if (entity.isPresent()) {
      return entity.get();
    } else {
      throw new EntityNotFoundException();
    }
  }

  @Override
  public EntityCollectionOperator<Entity> getOperator () {
    return _operator;
  }

  @Override
  public EntityCollection<Entity> apply (
    @NonNull final EntityCollectionOperator<Entity> operator
  ) {
    return new BaseEntityCollection<>(this, _operator.conjugate(operator));
  }

  @Override
  public <Output> EntityCollectionView<Output> apply (
    @NonNull final EntityCollectionTransformation<Entity, Output> transformation
  ) {
    // TODO Auto-generated method stub
    return null;
  }
}
