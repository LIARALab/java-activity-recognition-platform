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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;

import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.collection.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.view.AbstractEntityCollectionQueryBasedView;
import org.springframework.lang.NonNull;

/**
 * A sorted and filtered entity collection.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * @param <Entity> Type of entity in the collection.
 * @param <Identifier> Identifier type used for indexing the given entity type.
 */
public class      BaseEntityCollection<Entity> 
       extends    AbstractEntityCollectionQueryBasedView<Entity, Entity, List<Entity>>
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
    super(collection);
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
    super(collection);
    _operator = operator;
  }

  @Override
  public <Result> EntityCollectionMainQuery<Entity, Result> createCollectionQuery (
    @NonNull final Class<Result> result
  ) {
    final CriteriaQuery<Result> query = getManager().getCriteriaBuilder().createQuery(result);
    final EntityCollectionMainQuery<Entity, Result> collectionQuery = EntityCollectionQuery.from(
      getManager(), query, query.from(getQueryResultType())
    );
    
    _operator.apply(collectionQuery);
    
    if (result.isAssignableFrom(getQueryResultType())) {
      collectionQuery.getCriteriaQuery()
                     .select(collectionQuery.getEntity().as(result));
    }
    
    return collectionQuery;
  }
  
  @Override
  public TypedQuery<Entity> createTypedQuery () {
    return getManager().createQuery(createCollectionQuery().getCriteriaQuery());
  }

  @Override
  public <Identifier> Optional<Entity> findByIdentifier (@NonNull final Identifier identifier) {
    final EntityCollectionMainQuery<Entity, Entity> query = createCollectionQuery();
    final EntityType<Entity> entityType = getManager().getMetamodel().entity(getQueryResultType());
    
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
  public List<Entity> get () {
    return createTypedQuery().getResultList();
  }
}
