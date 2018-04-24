package org.liara.api.request.parser.transformation.grouping;

import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class      APIRequestConfigurationBasedGroupingProcessor<Entity>
       implements APIRequestGroupingProcessor<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _configuration;
  
  @Nullable
  private List<APIRequestGroupingProcessor<Entity>> _processors = null;

  public APIRequestConfigurationBasedGroupingProcessor (
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> configuration
  ) {
    _configuration = CollectionRequestConfiguration.fromClass(configuration);
  }
  
  public APIRequestConfigurationBasedGroupingProcessor (
    @NonNull final CollectionRequestConfiguration<Entity> configuration
  ) {
    _configuration = configuration;
  }
  
  @Override
  public EntityCollectionGroupTransformation<Entity> process (
    @NonNull final String key
  ) {
    if (_processors == null) {
      _processors = _configuration.createGroupingProcessors();
    }
    
    for (APIRequestGroupingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key)) {
        return processor.process(key);
      }
    }
    
    return null;
  }

  @Override
  public boolean hableToProcess (
    @NonNull final String key
  ) {
    if (_processors == null) {
      _processors = _configuration.createGroupingProcessors();
    }
    
    for (APIRequestGroupingProcessor<Entity> processor : _processors) {
      if (processor.hableToProcess(key)) {
        return true;
      }
    }
    
    return false;
  }

}
