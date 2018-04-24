package org.liara.api.collection.view;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.springframework.lang.NonNull;

public abstract class      AbstractEntityCollectionQueryBasedView<Entity, QueryResult, ViewResult>
                extends    AbstractCriteriaQueryBasedView<QueryResult, ViewResult>
                implements EntityCollectionQueryBasedView<Entity, QueryResult, ViewResult>
{
  public AbstractEntityCollectionQueryBasedView(@NonNull final CriteriaQueryBasedView<QueryResult, ?> view) {
    super(view);
  }

  public AbstractEntityCollectionQueryBasedView(
    @NonNull final EntityManager manager, 
    @NonNull final Class<QueryResult> resultType
  ) {
    super(manager, resultType);
  }

  @Override
  public <Result> CriteriaQuery<Result> createCriteriaQuery (@NonNull final Class<Result> result) {
    return createCollectionQuery(result).getCriteriaQuery();
  }

  @Override
  public EntityCollectionMainQuery<Entity, QueryResult> createCollectionQuery () {
    return createCollectionQuery(getQueryResultType());
  }
}
