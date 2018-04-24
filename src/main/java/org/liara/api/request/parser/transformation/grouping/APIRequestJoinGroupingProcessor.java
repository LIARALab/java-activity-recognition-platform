package org.liara.api.request.parser.transformation.grouping;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.grouping.ComposedGrouping;
import org.liara.api.collection.grouping.EntityGrouping;
import org.liara.api.collection.grouping.JoinGrouping;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.transformation.Transformation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestJoinGroupingProcessor<Entity, Joined> 
       implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityFieldSelector<Entity, Joined> _join;
  
  @NonNull
  private final APIRequestGroupingProcessor<Joined> _processor;
    
  public APIRequestJoinGroupingProcessor(
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Joined> join, 
    @NonNull final APIRequestGroupingProcessor<Joined> processor
  ) {
    _parameter = parameter;
    _join = join;
    _processor = processor;
  }

  @Override
  public Transformation<Entity, Tuple> process (
    @NonNull final String key
  ) {
    return _processor.process(key.substring(_parameter.length() + 1));
  }

  private List<APIRequestGroupingProcessor<Joined>> getProcessors () {
    if (_processors == null) {
      _processors = CollectionRequestConfiguration.fromClass(_configuration).createGroupingProcessors();
    }
    
    return _processors;
  }

  @Override
  public boolean hableToProcess (@NonNull final String key) {
    return key.startsWith(_alias + ".");
  }

}
