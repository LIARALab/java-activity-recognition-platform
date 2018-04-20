package org.liara.api.collection.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class      EntityCollectionCompoundTransformation<Input, Output>
       implements EntityCollectionTransformation<Input, Output>
{
  @NonNull
  private final List<EntityCollectionTransformation<Input, Output>> _transformations = new ArrayList<>();
  
  public EntityCollectionCompoundTransformation (
    @NonNull final Iterable<EntityCollectionTransformation<Input, Output>> transformations
  ) {
    Iterables.addAll(_transformations, transformations);
  }
  
  public EntityCollectionCompoundTransformation (
    @NonNull final Iterator<EntityCollectionTransformation<Input, Output>> transformations
  ) {
    Iterators.addAll(_transformations, transformations);
  }
  
  public EntityCollectionCompoundTransformation (
    @NonNull final EntityCollectionTransformation<Input, Output>[] transformations
  ) {
    _transformations.addAll(Arrays.asList(transformations));
  }

  @Override
  public EntityCollection<Input> transformCollection (@NonNull final EntityCollection<Input> collection) {
    EntityCollection<Input> result = collection;
    
    for (final EntityCollectionTransformation<Input, Output> transformation : _transformations) {
      result = transformation.transformCollection(result);
    }
    
    return result;
  }

  @Override
  public CriteriaQuery<Output> transformCriteria (
    @NonNull final EntityCollectionQuery<Input, Output> collectionQuery,
    @NonNull final CriteriaQuery<Output> query
  ) {    
    CriteriaQuery<Output> result = query;
    
    for (final EntityCollectionTransformation<Input, Output> transformation : _transformations) {
      result = transformation.transformCriteria(collectionQuery, result);
    }
    
    return result;
  }

  @Override
  public TypedQuery<Output> transformQuery (
    @NonNull final TypedQuery<Output> query
  ) {
    TypedQuery<Output> result = query;
    
    for (final EntityCollectionTransformation<Input, Output> transformation : _transformations) {
      result = transformation.transformQuery(result);
    }
    
    return result;
  }
}
