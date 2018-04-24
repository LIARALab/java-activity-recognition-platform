package org.liara.api.request.parser.transformation.grouping;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.grouping.EntityCollectionFieldGroupingTransformation;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;

public class      APIRequestExpressionGroupingProcessor<Entity> 
       implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final String _key;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _selector;
  
  public APIRequestExpressionGroupingProcessor (
    @NonNull final String key, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _key = key;
    _selector = selector;
  }
  
  public APIRequestExpressionGroupingProcessor (
    @NonNull final String key, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _key = key;
    _selector = selector;
  }

  @Override
  public EntityCollectionGroupTransformation<Entity> process (
    @NonNull final String key
  ) {
    return new EntityCollectionFieldGroupingTransformation<>(_selector);
  }

  @Override
  public boolean hableToProcess (@NonNull final String key) {
    return key.equals(_key);
  }
}
