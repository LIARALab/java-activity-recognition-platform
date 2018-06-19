package org.liara.api.test.collection.configuration;

import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.request.parser.operator.APIRequestEntityCollectionOperatorParser;
import org.liara.api.request.parser.operator.ordering.APIRequestOrderingProcessor;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;

import groovy.lang.Closure;

public class CollectionRequestConfigurationValidator<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity> _target;
  
  @NonNull
  private final APIRequestEntityCollectionOperatorParser<Entity> _filterParser;
  
  @NonNull
  private final List<APIRequestOrderingProcessor<Entity>> _orderings;
  
  public CollectionRequestConfigurationValidator (
    @NonNull final CollectionRequestConfiguration<Entity> target
  ) {
    _target = target;
    _filterParser = target.createFilterParser();
    _orderings = target.createOrderingProcessors();
    
  }
  
  public boolean itDeclareFieldNamed (
    @NonNull final String field,
    @NonNull final Closure<?> callback
  ) {
    final CollectionRequestConfigurationFieldValidator<Entity> result = new CollectionRequestConfigurationFieldValidator<>(
        this, field
    );
    
    Closures.callAs(callback, result);
    
    return result.isValid();
  }
  
  public CollectionRequestConfiguration<Entity> getTarget () {
    return _target;
  }

  public APIRequestEntityCollectionOperatorParser<Entity> getFilterParser () {
    return _filterParser;
  }

}
