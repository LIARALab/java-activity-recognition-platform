package org.liara.api.request.parser.filtering;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.filtering.CustomHavingBasedEntityFilter;
import org.liara.api.collection.filtering.EmptyEntityFilter;
import org.liara.api.collection.filtering.EntityFilter;
import org.liara.api.criteria.CustomCollectionRelation;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestCustomHavingBasedEntityFilterParser<Entity, Joined> implements APIRequestEntityFilterParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final Class<Joined> _joined;
  
  @NonNull
  private final CustomCollectionRelation<Entity, Joined> _relation;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;

  public APIRequestCustomHavingBasedEntityFilterParser(
    @NonNull final String field,
    @NonNull final Class<Joined> joined,
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration 
  ) {
    _field = field;
    _joined = joined;
    _relation = relation;
    _configuration = configuration;
  }

  @Override
  public EntityFilter<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      final EntityFilter<Joined> filter = CollectionRequestConfiguration.fromClass(
        _configuration
      ).createFilterParser().parse(subRequest);
      
      return new CustomHavingBasedEntityFilter<>(_relation, _joined, filter);
    } else {
      return new EmptyEntityFilter<>();
    }
  }

}
