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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;
import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.collection.filtering.ComposedEntityFilter;
import org.liara.api.collection.filtering.ASTBasedEntityFilter;
import org.liara.api.collection.filtering.EntityFilter;
import org.liara.api.collection.grouping.EntityGrouping;
import org.liara.api.collection.ordering.Ordering;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.lang.NonNull;

/**
 * A collection of entity in the API.
 * 
 * Expose common requests and help you to construct more advanced research.
 *
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 * @param <Entity> Type of entity in the collection.
 * @param <Identifier> Identifier type used for indexing the given entity type.
 */
public interface EntityCollection<Entity, Identifier>
{
  /**
   * Return the size of the collection.
   *
   * @return The size of the collection.
   */
  public default long getSize () {
    final EntityManager entityManager = this.getEntityManager();
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final EntityCollectionQuery<Entity, Long> query = this.createCollectionQuery(Long.class);
    
    query.select(criteriaBuilder.count(query.getEntity()));
    return entityManager.createQuery(query).getSingleResult();
  }

  /**
   * Return a view of this collection.
   *
   * @param cursor Cursor used in order to define the view.
   *
   * @return A view of this collection.
   */
  public default EntityCollectionView<Entity, Identifier> getView (@NonNull final Cursor cursor) {
    return new EntityCollectionView<>(this, cursor);
  }

  /**
   * Return the content of this collection.
   *
   * @return The content of this collection.
   */
  public default List<Entity> getContent () {
    return this.createQuery().getResultList();
  }

  /**
   * Try to find an entity of the collection.
   *
   * @param identifier Identifier of the entity to get.
   */
  public default Entity findById (@NonNull final Identifier identifier) {
    final EntityManager entityManager = this.getEntityManager();
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final EntityType<Entity> entityType = entityManager.getMetamodel().entity(this.getEntityClass());

    final EntityCollectionQuery<Entity, Entity> criteriaQuery = this.createCollectionQuery();
    
    criteriaQuery.where(
      criteriaBuilder.equal(
        criteriaQuery.getEntity().get(entityType.getId(identifier.getClass())),
        identifier
      )
    );
    
    return entityManager.createQuery(criteriaQuery).getSingleResult();
  }

  /**
   * Try to find an entity of the collection.
   *
   * @throws EntityNotFoundException 
   * 
   * @param identifier Identifier of the entity to get.
   */
  public default Entity findByIdOrFail (@NonNull final Identifier identifier) throws EntityNotFoundException {
    final Entity entity = this.findById(identifier);

    if (entity != null) {
      return entity;
    } else {
      throw new EntityNotFoundException();
    }
  }

  /**
   * Return an entity of the collection.
   *
   * @param index Position of the entity to return in this collection.
   */
  public default Entity get (final int index) {
    return this.createQuery().setFirstResult(index).setMaxResults(1).getSingleResult();
  }

  /**
   * Return an entity of the collection.
   *
   * @throws EntityNotFoundException 
   * 
   * @param index Position of the entity to return in this collection.
   */
  public default Entity getOrFail (final int index) throws EntityNotFoundException {
    final Entity entity = this.get(index);
    
    if (entity != null) {
      return entity;
    } else {
      throw new EntityNotFoundException();
    }
  }

  /**
   * Return a query over this collection.
   *
   * @return A query over this collection.
   */
  public default TypedQuery<Entity> createQuery () {
    return this.getEntityManager().createQuery(this.createCollectionQuery());
  }

  /**
   * Return a criteria query over this collection.
   *
   * A criteria query can be muted.
   *
   * @return A criteria query over this collection.
   */
  public default EntityCollectionQuery<Entity, Entity> createCollectionQuery () {
    final EntityCollectionQuery<Entity, Entity> query = this.createCollectionQuery(this.getEntityClass());
    query.select(query.getEntity());
    getOrdering().order(getCriteriaBuilder(), query);
    return query;
  }

  /**
   * Return a criteria query over this collection with a custom return type.
   *
   * A criteria query can be muted.
   *
   * @param clazz The custom return type of the query.
   *
   * @return A criteria query over this collection with a custom return type.
   */
  public <U> EntityCollectionQuery<Entity, U> createCollectionQuery (@NonNull final Class<U> clazz);
  
  /**
   * Return the entity manager that manage this collection.
   * 
   * @return The entity manager that manage this collection.
   */
  public EntityManager getEntityManager ();
  
  /**
   * An helper method in order to retrieve only the criteria builder of this collection.
   * 
   * @return The criteria builder of this collection.
   */
  public default CriteriaBuilder getCriteriaBuilder () {
    return this.getEntityManager().getCriteriaBuilder();
  }
  
  /**
   * Return the class of entities stored in this collection.
   * 
   * @return The class of entities stored in this collection.
   */
  public Class<Entity> getEntityClass();
  
  /**
   * An helper class for retrieving type of entities stored into this collection.
   * 
   * @return The stored entities type.
   */
  public default EntityType<Entity> getEntityType() {
    return this.getEntityManager().getMetamodel().entity(this.getEntityClass());
  }
  
  /**
   * Return a new filtered collection based on this one.
   * 
   * @param filter Filter to apply.
   * @return A new filtered collection based on this one.
   */
  public EntityCollection<Entity, Identifier> order (@NonNull final Ordering<Entity> ordering);

  /**
   * Return a new filtered collection based on this one.
   * 
   * @param filter Filter to apply.
   * @return A new filtered collection based on this one.
   */
  public default EntityCollection<Entity, Identifier> filter (@NonNull final EntityFilter<Entity> filter) {
    return new FilteredEntityCollection<>(getEntityClass(), filter, getEntityManager());
  }
  
  /**
   * Try to filter this collection with the default CollectionRequestConfiguration (if any).
   * 
   * @throws InvalidAPIRequestException 
   * 
   * @param request Request to use in order to filter this collection.
   * @return A new collection filtered according to the given request.
   */
  public default EntityCollection<Entity, Identifier> filter (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    final CollectionRequestConfiguration<Entity> configuration = CollectionRequestConfiguration.getDefault(this);
    return configuration.filterCollection(request, this);
  }
  

  /**
   * Try to order this collection with the default CollectionRequestConfiguration (if any).
   * 
   * @throws InvalidAPIRequestException 
   * 
   * @param request Request to use in order to filter this collection.
   * @return A new collection filtered according to the given request.
   */
  public default EntityCollection<Entity, Identifier> order (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    final CollectionRequestConfiguration<Entity> configuration = CollectionRequestConfiguration.getDefault(this);
    return configuration.orderCollection(request, this);
  }
 
  public default <Value> EntityCollectionQuery<Entity, Tuple> group (
    @NonNull final EntityCollectionQuery<Entity, Tuple> query, 
    @NonNull final APIRequest request,
    @NonNull final CriteriaExpressionSelector<Value> selection
  ) {
    final CollectionRequestConfiguration<Entity> configuration = CollectionRequestConfiguration.getDefault(this);
    final EntityGrouping<Entity> grouping = configuration.parseGrouping(request);
    final List<Selection<?>> selections = grouping.createSelection(getCriteriaBuilder(), query);
    
    if (selections.size() > 0) {
      selections.add(selection.select(getCriteriaBuilder(), query, query.getEntity()));
      query.multiselect(selections);
      query.groupBy(grouping.createGroupBy(getCriteriaBuilder(), query)); 
    } else {
      query.multiselect(selection.select(getCriteriaBuilder(), query, query.getEntity()));
    }
    
    return query;
  }
  
  /**
   * Try to apply a request to this collection with the default CollectionRequestConfiguration (if any).
   * 
   * @throws InvalidAPIRequestException 
   * 
   * @param request Request to use in order to filter this collection.
   * @return A new view over a collection according to the given request.
   */
  public default EntityCollectionView<Entity, Identifier> apply (@NonNull final APIRequest request) throws InvalidAPIRequestException {
    final CollectionRequestConfiguration<Entity> configuration = CollectionRequestConfiguration.getDefault(this);
    return configuration.applyRequest(request, this);
  }
  
  /**
   * Try to apply a request to this collection with the default CollectionRequestConfiguration (if any).
   * 
   * @throws InvalidAPIRequestException 
   * 
   * @param request Request to use in order to filter this collection.
   * @return A new view over a collection according to the given request.
   */
  public default <Result> List<Result> fetch (@NonNull final EntityCollectionQuery<Entity, Result> query) {
    return getEntityManager().createQuery(query).getResultList();
  }
  
  public Ordering<Entity> getOrdering ();
}
