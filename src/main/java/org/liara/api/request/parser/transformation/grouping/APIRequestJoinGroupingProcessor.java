package org.liara.api.request.parser.transformation.grouping;

import javax.persistence.criteria.Join;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.liara.api.collection.transformation.grouping.EntityCollectionJoinGroupingTransformation;
import org.springframework.lang.NonNull;

public class      APIRequestJoinGroupingProcessor<Entity, Joined> 
       implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @NonNull
  private final APIRequestGroupingProcessor<Joined> _processor;
    
  public APIRequestJoinGroupingProcessor(
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final APIRequestGroupingProcessor<Joined> processor
  ) {
    _parameter = parameter;
    _join = join;
    _processor = processor;
  }

  @Override
  public EntityCollectionGroupTransformation<Entity> process (
    @NonNull final String key
  ) {
    return new EntityCollectionJoinGroupingTransformation<Entity, Joined>(
        _join,
        _processor.process(key.substring(_parameter.length() + 1))
    );
  }

  @Override
  public boolean hableToProcess (@NonNull final String key) {
    return key.startsWith(_parameter + ".");
  }

}
