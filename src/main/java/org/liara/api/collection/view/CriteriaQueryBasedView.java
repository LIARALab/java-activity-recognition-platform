package org.liara.api.collection.view;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.lang.NonNull;

public interface CriteriaQueryBasedView<QueryResult, ViewResult> extends View<ViewResult>
{
  public TypedQuery<QueryResult> createTypedQuery ();
  
  public CriteriaQuery<QueryResult> createCriteriaQuery ();
  
  public <Result> CriteriaQuery<Result> createCriteriaQuery (@NonNull final Class<Result> result);
  
  public Class<QueryResult> getQueryResultType ();
  
  public EntityManager getManager ();
}
