package org.liara.api.collection.view;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.springframework.lang.NonNull;

public interface EntityCollectionQueryBasedView<Entity, QueryResult, ViewResult> 
       extends   CriteriaQueryBasedView<QueryResult, ViewResult>
{ 
  /**
   * Create an return a collection query that select all entities of this collection and return them.
   * 
   * @return A collection query that select all entities of this collection and return them.
   */
  public EntityCollectionMainQuery<Entity, QueryResult> createCollectionQuery ();

  /**
   * Create and return a collection query that select all entities of this collection and 
   * return a result of a given type.
   * 
   * @param result The result type of the query.
   * 
   * @return A collection query that select all entities of this collection and return a result of a given type.
   */
  public <Result> EntityCollectionMainQuery<Entity, Result> createCollectionQuery (
    @NonNull final Class<Result> result
  );
}
