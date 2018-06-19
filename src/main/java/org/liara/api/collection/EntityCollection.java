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
package org.liara.api.collection;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

/**
 * A collection of entity.
 *
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Type of entity in the collection.
 */
public class EntityCollection<Entity>
       implements View<List<Entity>>
{ 
  /**
   * Manager related to this collection.
   */
  @NonNull
  private final EntityManagerFactory _entityManagerFactory;
  
  /**
   * Type of entity selected by this collection.
   */
  @NonNull
  private final Class<Entity> _contentType;
  
  /**
   * Operator to apply to a query in order to select the collection content.
   */
  @NonNull
  private final EntityCollectionConjunctionOperator<Entity> _operator;
  
  /**
   * Create a collection for a given type and a given manager.
   * 
   * @param entity Entity type to store.
   * @param entityManager Entity Manager that manage the given type.
   */
  public EntityCollection (
    @NonNull final EntityManagerFactory entityManagerFactory,
    @NonNull final Class<Entity> entity
  ) {
    _entityManagerFactory = entityManagerFactory;
    _contentType = entity;
    _operator = new EntityCollectionConjunctionOperator<>();
  }
  
  /**
   * Create a copy of another collection.
   * 
   * @param collection Collection to copy.
   */
  public EntityCollection (
    @NonNull final EntityCollection<Entity> collection
  ) {
    _entityManagerFactory = collection.getManagerFactory();
    _contentType = collection.getEntityType();
    _operator = new EntityCollectionConjunctionOperator<>(collection.getOperator().iterator());
  }
  
  /**
   * Create a copy of another collection and change the collection operator.
   * 
   * @param collection Collection to copy.
   * @param operator New operator to apply.
   */
  public EntityCollection (
    @NonNull final EntityCollection<Entity> collection,
    @NonNull final EntityCollectionConjunctionOperator<Entity> operator
  ) {
    _entityManagerFactory = collection.getManagerFactory();
    _contentType = collection.getEntityType();
    _operator = operator;
  }
  
  /**
   * Try to find an entity of this collection by using it's identifier.
   * 
   * @param identifier Identifier of the entity to find.
   * @return An optional value with the fetched entity if exists or empty otherwise.
   */
  public <Identifier> Optional<Entity> findByIdentifier (
    @NonNull final Identifier identifier
  ) {
    final EntityCollectionMainQuery<Entity, Entity> query = createCollectionQuery(getEntityType());
    query.select(query.getEntity());
    
    final EntityType<Entity> entityType = query.getManager()
                                               .getMetamodel()
                                               .entity(getEntityType());
    
    query.andWhere(
      query.getManager().getCriteriaBuilder().equal(
        query.getEntity().get(entityType.getId(identifier.getClass())), 
        identifier
      )
    );
    
    return query.fetchFirstAndClose();
  }
  
  public <Identifier> boolean containsEntityWithIdentifier (
    @NonNull final Identifier identifier
  ) {
    return findByIdentifier(identifier).isPresent();
  }
  
  /**
   * Try to find an entity of this collection by using it's identifier.
   * 
   * @param identifier Identifier of the entity to find.
   * @return An optional value with the fetched entity if exists or empty otherwise.
   * 
   * @throws EntityNotFoundException If the requested entity does not exists in this collection.
   */
  public <Identifier> Entity findByIdentifierOrFail (
    @NonNull final Identifier identifier
  ) throws EntityNotFoundException {
    final Optional<Entity> entity = findByIdentifier(identifier);
    
    if (entity.isPresent()) {
      return entity.get();
    } else {
      throw new EntityNotFoundException();
    }
  }

  /**
   * Return an operator to apply to a given query in order to select all entities of this collection.
   * 
   * @return An operator to apply to a given query in order to select all entities of this collection.
   */
  public EntityCollectionConjunctionOperator<Entity> getOperator ()  {
    return _operator;
  }
  
  /**
   * Apply an operator to this collection and return a new updated instance of this collection.
   * 
   * @param operator Operator to apply to this collection.
   * 
   * @return A new updated instance of this collection.
   */
  public EntityCollection<Entity> apply (
    @NonNull final EntityCollectionOperator<Entity> operator
  ) {
    return new EntityCollection<>(this, _operator.conjugate(operator));
  }
  
  /**
   * Create a collection query that select all entities of this collection and return a result of a given type.
   * 
   * @param result Result type of the query.
   * 
   * @return A filtered query that select all entities of this collection and return a result of a given type.
   */
  public <Result> EntityCollectionMainQuery<Entity, Result> createCollectionQuery (
    @NonNull final Class<Result> result
  ) {
    final EntityManager manager = _entityManagerFactory.createEntityManager();
    final CriteriaQuery<Result> query = manager.getCriteriaBuilder().createQuery(result);
    final EntityCollectionMainQuery<Entity, Result> collectionQuery = EntityCollectionQuery.from(
      manager, query, query.from(getEntityType())
    );
    
    _operator.apply(collectionQuery);
    
    return collectionQuery;
  }
  
  /**
   * Alias of createCollectionQuery(getEntityType()).
   * 
   * @return A filtered query that select all entities of this collection and return a result of a given type.
   */
  public EntityCollectionMainQuery<Entity, Entity> createCollectionQuery () {
    return createCollectionQuery(getEntityType());
  }
  
  /**
   * Return the number of elements selected by this collection.
   * 
   * @return The number of elements selected by this collection.
   */
  public long getSize () {
    final EntityCollectionMainQuery<Entity, Long> query = createCollectionQuery(Long.class);
    query.select(
      query.getManager()
           .getCriteriaBuilder()
           .count(query.getEntity())
    );
    
    return query.fetchFirstAndClose().get().longValue();
  }

  @Override
  public List<Entity> get () {
    final EntityCollectionMainQuery<Entity, Entity> query = createCollectionQuery(getEntityType());
    query.select(query.getEntity());
    
    return query.fetchAllAndClose();
  }
  
  /**
   * Return an element of this collection.
   * 
   * @param index Index of the element to retrieve, starting at 0.
   * 
   * @return The element at the given index.
   * 
   * @throws IndexOutOfBoundsException If the index is less than 0 or greather or equal to the collection size.
   */
  public Entity get (final long index) throws IndexOutOfBoundsException {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Invalid index : the given number is less than 0.");
    } else {
      final long size = getSize();
      
      if (index >= size) {
        throw new IndexOutOfBoundsException("Invalid index : the given number is greather than or equal to" + size + ".");
      }
      
      final EntityCollectionMainQuery<Entity, Entity> query = createCollectionQuery(getEntityType());
      query.select(query.getEntity());
      
      /**
       * @todo Check long limit.
       */
      return query.fetchOneAndClose((int) index).get();
    }
  }
  
  public Optional<Entity> first () {
    final EntityCollectionMainQuery<Entity, Entity> query = createCollectionQuery();    
    return query.fetchOneAndClose(0);
  }
  
  /**
   * Return this collection's related manager factory.
   * 
   * @return This collection's related manager factory.
   */
  public EntityManagerFactory getManagerFactory () {
    return _entityManagerFactory;
  }
  
  /**
   * Return the content type of this collection.
   * 
   * @return The content type of this collection.
   */
  public Class<Entity> getEntityType () {
    return _contentType;
  }
}
