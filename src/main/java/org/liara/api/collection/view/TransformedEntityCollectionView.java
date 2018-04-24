package org.liara.api.collection.view;

import javax.persistence.TypedQuery;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.transformation.EntityCollectionQueryTransformation;
import org.springframework.lang.NonNull;

public abstract class      TransformedEntityCollectionView<Entity, QueryResult, ViewResult>
                extends    AbstractCriteriaQueryBasedView<QueryResult, ViewResult>
                implements EntityCollectionQueryBasedView<Entity, QueryResult, ViewResult>
{
  @NonNull
  private final EntityCollectionQueryBasedView<Entity, QueryResult, ?> _parentView;
  
  @NonNull
  private final EntityCollectionQueryTransformation<Entity, QueryResult> _transformation;
  
  public TransformedEntityCollectionView(
    @NonNull final EntityCollectionQueryBasedView<Entity, QueryResult, ?> parentView,
    @NonNull final EntityCollectionQueryTransformation<Entity, QueryResult> transformation
  ) {
    super(parentView.getManager(), transformation.getResultType());
    _parentView = parentView;
    _transformation = transformation;
  }

  @Override
  public TypedQuery<QueryResult> createTypedQuery () {
    return getManager().createQuery(
      createCollectionQuery().getCriteriaQuery()
    );
  }

  @Override
  public EntityCollectionMainQuery<Entity, QueryResult> createCollectionQuery () {
    final EntityCollectionMainQuery<Entity, QueryResult> result = _parentView.createCollectionQuery();
    _transformation.apply(result);
    return result;
  }
}
