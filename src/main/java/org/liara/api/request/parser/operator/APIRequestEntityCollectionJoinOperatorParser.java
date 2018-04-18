package org.liara.api.request.parser.operator;

import javax.persistence.criteria.Join;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.operator.EntityCollectionFilteringOperator;
import org.liara.api.collection.operator.EntityCollectionJoinOperator;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.operator.IdentityOperator;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestEntityCollectionJoinOperatorParser<Entity, Joined> 
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;

  public APIRequestEntityCollectionJoinOperatorParser(
    @NonNull final String field,
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration 
  ) {
    _field = field;
    _join = join;
    _configuration = configuration;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      final EntityCollectionFilteringOperator<Joined> filter = CollectionRequestConfiguration.fromClass(
        _configuration
      ).createFilterParser().parse(subRequest);
      
      return new EntityCollectionJoinOperator<>(_join, filter);
    } else {
      return new IdentityOperator<>();
    }
  }
}
