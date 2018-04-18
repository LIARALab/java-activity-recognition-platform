package org.liara.api.request.parser.ordering;

import java.util.ArrayList;
import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.operator.ordering.ComposedOrdering;
import org.liara.api.collection.operator.ordering.JoinOrdering;
import org.liara.api.collection.operator.ordering.Ordering;
import org.liara.api.collection.operator.ordering.OrderingDirection;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class APIRequestJoinOrderingProcessor<Entity, Joined> implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final String _join;
  
  @NonNull
  private final String _alias;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;
  
  @Nullable
  private List<APIRequestOrderingProcessor<Joined>> _processors = null;
    
  public APIRequestJoinOrderingProcessor(
    @NonNull final String join, 
    @NonNull final String alias, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    _join = join;
    _alias = alias;
    _configuration = configuration;
  }

  @Override
  public Ordering<Entity> process (
    @NonNull final String key, 
    @NonNull final OrderingDirection direction
  ) {
    final String childKey = key.substring(_alias.length() + 1);
    final List<Ordering<Joined>> results = new ArrayList<>();
    
    for (final APIRequestOrderingProcessor<Joined> processor : getProcessors()) {
      if (processor.hableToProcess(childKey, direction)) {
        results.add(processor.process(childKey, direction));
      }
    }
    
    return new JoinOrdering<>(_join, new ComposedOrdering<>(results));
  }

  private List<APIRequestOrderingProcessor<Joined>> getProcessors () {
    if (_processors == null) {
      _processors = CollectionRequestConfiguration.fromClass(_configuration).createOrderingProcessors();
    }
    
    return _processors;
  }

  @Override
  public boolean hableToProcess (@NonNull final String key, @NonNull final OrderingDirection direction) {
    return key.startsWith(_alias + ".");
  }

}
