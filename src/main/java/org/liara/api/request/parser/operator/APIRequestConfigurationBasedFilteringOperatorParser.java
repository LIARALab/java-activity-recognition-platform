package org.liara.api.request.parser.operator;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestConfigurationBasedFilteringOperatorParser<Entity>
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _configuration;

  public APIRequestConfigurationBasedFilteringOperatorParser(
    @NonNull final Class<? extends CollectionRequestConfiguration<Entity>> configuration
  ) {
    _configuration = CollectionRequestConfiguration.fromClass(configuration);
  }
  
  public APIRequestConfigurationBasedFilteringOperatorParser(
    @NonNull final CollectionRequestConfiguration<Entity> configuration
  ) {
    _configuration = configuration;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    return _configuration.createFilterParser().parse(request);
  }
}
