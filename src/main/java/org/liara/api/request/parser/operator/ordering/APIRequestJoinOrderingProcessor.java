package org.liara.api.request.parser.operator.ordering;

import javax.persistence.criteria.Join;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionJoinOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestJoinOrderingProcessor<Entity, Joined> 
       implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @Nullable
  private APIRequestOrderingProcessor<Joined> _processor = null;
    
  public APIRequestJoinOrderingProcessor(
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final APIRequestOrderingProcessor<Joined> processor
  ) {
    _parameter = parameter;
    _join = join;
    _processor = processor;
  }

  @Override
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    final String childKey = key.substring(_parameter.length() + 1);
    
    return new EntityCollectionJoinOperator<>(
        _join, _processor.process(childKey, direction)
    );
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key, 
    @NonNull final EntityCollectionOrderingOperator.Direction direction
  ) {
    if (key.startsWith(_parameter + ".")) {
      final String childKey = key.substring(_parameter.length() + 1);
      
      return _processor.hableToProcess(childKey, direction);
    } else {
      return false;
    }
  }
}
