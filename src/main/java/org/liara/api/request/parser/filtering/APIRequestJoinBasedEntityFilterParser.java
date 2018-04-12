package org.liara.api.request.parser.filtering;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.filtering.EmptyEntityFilter;
import org.liara.api.collection.filtering.EntityFilter;
import org.liara.api.collection.filtering.JoinBasedEntityFilter;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestJoinBasedEntityFilterParser<Entity, Joined> implements APIRequestEntityFilterParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final String _join;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;

  public APIRequestJoinBasedEntityFilterParser(
    @NonNull final String field,
    @NonNull final String join,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration 
  ) {
    _field = field;
    _join = join;
    _configuration = configuration;
  }

  @Override
  public EntityFilter<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      final EntityFilter<Joined> filter = CollectionRequestConfiguration.fromClass(
        _configuration
      ).createFilterParser().parse(subRequest);
      
      return new JoinBasedEntityFilter<>(_join, filter);
    } else {
      return new EmptyEntityFilter<>();
    }
  }

}
