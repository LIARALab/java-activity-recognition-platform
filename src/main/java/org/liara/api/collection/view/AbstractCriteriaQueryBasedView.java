package org.liara.api.collection.view;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.lang.NonNull;

public abstract class      AbstractCriteriaQueryBasedView<QueryResult, ViewResult> 
                extends    AbstractView<ViewResult>
                implements CriteriaQueryBasedView<QueryResult, ViewResult>
{
  @NonNull
  private final EntityManager _manager;
  
  @NonNull
  private final Class<QueryResult> _resultType;
  
  public AbstractCriteriaQueryBasedView (
    @NonNull final CriteriaQueryBasedView<QueryResult, ?> view
  ) {
   _manager = view.getManager();
   _resultType = view.getQueryResultType();
  }
  
  public AbstractCriteriaQueryBasedView (
    @NonNull final EntityManager manager,
    @NonNull final Class<QueryResult> resultType
  ) {
   _manager = manager;
   _resultType = resultType;
  }
  
  @Override
  public TypedQuery<QueryResult> createTypedQuery () {
    return _manager.createQuery(createCriteriaQuery());
  }

  @Override
  public CriteriaQuery<QueryResult> createCriteriaQuery () {
    return createCriteriaQuery(_resultType);
  }

  @Override
  public Class<QueryResult> getQueryResultType () {
    return _resultType;
  }

  @Override
  public EntityManager getManager () {
    return _manager;
  }
}
