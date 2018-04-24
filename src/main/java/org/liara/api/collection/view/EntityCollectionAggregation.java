package org.liara.api.collection.view;

import javax.persistence.EntityManager;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.transformation.aggregation.EntityAggregationTransformation;
import org.springframework.lang.NonNull;

public class EntityCollectionAggregation<Entity, AggregationType> implements View<AggregationType>
{
  @NonNull
  private final EntityAggregationTransformation<Entity, AggregationType> _aggregator;
  
  @NonNull
  private final EntityCollection<Entity> _aggregated;
  
  public EntityCollectionAggregation (
    @NonNull final EntityCollection<Entity> aggregated,
    @NonNull final EntityAggregationTransformation<Entity, AggregationType> aggregator
  ) {
    _aggregated = aggregated;
    _aggregator = aggregator;
  }
 
  /**
   * Create a collection query that select the given aggregation.
   * 
   * @param result Result type of the query.
   * 
   * @return A filtered query that select all entities of this collection and return a result of a given type.
   */
  public <Result> EntityCollectionMainQuery<Entity, Result> createCollectionQuery (
    @NonNull final Class<Result> result
  ) {
    final EntityCollectionMainQuery<Entity, Result> query = _aggregated.createCollectionQuery(result);
    
    _aggregator.apply(query);
    
    return query;
  }
  
  @Override
  public AggregationType get () {
    final EntityCollectionMainQuery<Entity, AggregationType> query = createCollectionQuery(
      _aggregator.getAggregationType()
    );
    
    return query.getManager().createQuery(query.getCriteriaQuery()).getSingleResult();
  }
  
  public EntityManager getManager () {
    return _aggregated.getManager();
  }
  
  public EntityCollection<Entity> getAggregated () {
    return _aggregated;
  }
  
  public EntityAggregationTransformation<Entity, AggregationType> getAggregator () {
    return _aggregator;
  }
  
  public Class<AggregationType> getAggregationType () {
    return _aggregator.getAggregationType();
  }
}
