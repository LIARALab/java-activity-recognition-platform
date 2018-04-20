package org.liara.api.collection.transformation;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.view.EntityCollectionView;
import org.springframework.lang.NonNull;

public interface EntityCollectionTransformation<Input, Output>
{ 
  public default EntityCollection<Input> transformCollection (
    @NonNull final EntityCollection<Input> collection
  ) {
    return collection;
  }
  
  public default CriteriaQuery<Output> transformCriteria (
    @NonNull final EntityCollectionQuery<Input, Output> collectionQuery,
    @NonNull final CriteriaQuery<Output> query
  ) {
    return query;
  }
  
  public default TypedQuery<Output> transformQuery (
    @NonNull final TypedQuery<Output> query
  ) {
    return query;
  }
  
  public default EntityCollectionView<Output> apply (
    @NonNull final EntityCollection<Input> collection
  ) {
    return collection.apply(this);
  }
}
