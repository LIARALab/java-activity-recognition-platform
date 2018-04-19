package org.liara.api.request.parser.filtering;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.operator.EntityCollectionExistsOperator;
import org.liara.api.collection.operator.EntityCollectionFilteringOperator;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.operator.IdentityOperator;
import org.liara.api.collection.query.relation.EntityRelation;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.operator.APIRequestEntityCollectionOperatorParser;
import org.springframework.lang.NonNull;

public class      APIRequestEntityCollectionExistsOperatorParser<Entity, Joined> 
       implements APIRequestEntityCollectionFilteringOperatorParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final Class<Joined> _joined;
  
  @NonNull
  private final EntityRelation<Entity, Joined> _relation;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<Joined>> _configuration;

  public APIRequestEntityCollectionExistsOperatorParser(
    @NonNull final String field,
    @NonNull final Class<Joined> joined,
    @NonNull final EntityRelation<Entity, Joined> relation,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration 
  ) {
    _field = field;
    _joined = joined;
    _relation = relation;
    _configuration = configuration;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      final EntityCollectionFilteringOperator<Joined> filter = CollectionRequestConfiguration.fromClass(
        _configuration
      ).createFilterParser().parse(subRequest);
      
      return new EntityCollectionExistsOperator<>(_joined, _relation, filter);
    } else {
      return new IdentityOperator<>();
    }
  }
}
