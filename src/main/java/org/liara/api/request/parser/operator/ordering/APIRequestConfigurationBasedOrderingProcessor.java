package org.liara.api.request.parser.operator.ordering;

import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator.Direction;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestConfigurationBasedOrderingProcessor<Entity>
       implements APIRequestOrderingProcessor<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _configuration;
  
  @Nullable
  private List<APIRequestOrderingProcessor<Entity>> _processors = null;

  public APIRequestConfigurationBasedOrderingProcessor (
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> configuration
  ) {
    _configuration = CollectionRequestConfiguration.fromClass(configuration);
  }
  
  public APIRequestConfigurationBasedOrderingProcessor (
    @NonNull final CollectionRequestConfiguration<Entity> configuration
  ) {
    _configuration = configuration;
  }
  
  @Override
  public EntityCollectionOperator<Entity> process (
    @NonNull final String key, 
    @NonNull final Direction direction
  ) {
    if (_processors == null) {
      _processors = _configuration.createOrderingProcessors();
    }
    
    for (APIRequestOrderingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key, direction)) {
        return processor.process(key, direction);
      }
    }
    
    return new EntityCollectionIdentityOperator<>();
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key, 
    @NonNull final Direction direction
  ) {
    if (_processors == null) {
      _processors = _configuration.createOrderingProcessors();
    }
    
    for (APIRequestOrderingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key, direction)) {
        return true;
      }
    }
    
    return false;
  }

}
