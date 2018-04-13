package org.liara.api.request.parser.grouping;

import java.util.ArrayList;
import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.grouping.ComposedGrouping;
import org.liara.api.collection.grouping.EntityGrouping;
import org.liara.api.collection.grouping.JoinGrouping;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class APIRequestJoinGroupingProcessor<Entity, Joined> implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final String _join;
  
  @NonNull
  private final String _alias;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;
  
  @Nullable
  private List<APIRequestGroupingProcessor<Joined>> _processors = null;
    
  public APIRequestJoinGroupingProcessor(
    @NonNull final String join, 
    @NonNull final String alias, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    _join = join;
    _alias = alias;
    _configuration = configuration;
  }

  @Override
  public EntityGrouping<Entity> process (
    @NonNull final String key
  ) {
    final String childKey = key.substring(_alias.length() + 1);
    final List<EntityGrouping<Joined>> results = new ArrayList<>();
    
    for (final APIRequestGroupingProcessor<Joined> processor : getProcessors()) {
      if (processor.hableToProcess(childKey)) {
        results.add(processor.process(childKey));
      }
    }
    
    return new JoinGrouping<>(_join, new ComposedGrouping<>(results));
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
