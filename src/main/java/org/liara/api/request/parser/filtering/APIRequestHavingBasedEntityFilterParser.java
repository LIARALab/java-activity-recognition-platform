package org.liara.api.request.parser.filtering;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.filtering.EmptyEntityFilter;
import org.liara.api.collection.filtering.EntityFilter;
import org.liara.api.collection.filtering.HavingBasedEntityFilter;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestHavingBasedEntityFilterParser<Entity, Joined> implements APIRequestEntityFilterParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final String _having;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;

  public APIRequestHavingBasedEntityFilterParser(
    @NonNull final String field,
    @NonNull final String having,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration 
  ) {
    _field = field;
    _having = having;
    _configuration = configuration;
  }

  @Override
  public EntityFilter<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      final EntityFilter<Joined> filter = CollectionRequestConfiguration.fromClass(
        _configuration
      ).createFilterParser().parse(subRequest);
      
      return new HavingBasedEntityFilter<>(_having, filter);
    } else {
      return new EmptyEntityFilter<>();
    }
  }
}
