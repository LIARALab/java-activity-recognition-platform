package org.liara.api.request.parser.operator.ordering;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.operator.EntityCollectionOrderingOperator;
import org.liara.api.collection.operator.EntityCollectionExpressionOrderingOperator;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class      APIRequestFieldOrderingProcessor<Entity> 
       implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _selector;
  
  public APIRequestFieldOrderingProcessor (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    _parameter = parameter;
    _selector = selector;
  }

  @Override
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(_selector, direction);
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key,
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    return key.equals(_parameter);
  }
}
