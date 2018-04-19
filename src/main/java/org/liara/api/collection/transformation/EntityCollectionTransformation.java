package org.liara.api.collection.transformation;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public interface EntityCollectionTransformation<Input, Output>
{  
  public CriteriaQuery<Output> transformCriteria (
    @NonNull final EntityCollectionQuery<Input> collectionQuery,
    @NonNull final CriteriaQuery<Output> query
  );
  
  public TypedQuery<Output> transformQuery (
    @NonNull final TypedQuery<Output> query
  );
}
