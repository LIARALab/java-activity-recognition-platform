package org.liara.api.request.parser.operator;

import org.liara.api.collection.query.relation.EntityRelation;
import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.filtering.EntityCollectionExistsOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestEntityCollectionExistsOperatorParser<Entity, Joined> 
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final Class<Joined> _joined;
  
  @NonNull
  private final EntityRelation<Entity, Joined> _relation;
  
  @NonNull
  private final APIRequestEntityCollectionOperatorParser<Joined> _joinParser;

  public APIRequestEntityCollectionExistsOperatorParser(
    @NonNull final String field,
    @NonNull final Class<Joined> joined,
    @NonNull final EntityRelation<Entity, Joined> relation,
    @NonNull final APIRequestEntityCollectionOperatorParser<Joined> joinParser 
  ) {
    _field = field;
    _joined = joined;
    _relation = relation;
    _joinParser = joinParser;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      return new EntityCollectionExistsOperator<>(
          _joined, 
          _relation, 
          _joinParser.parse(subRequest)
      );
    } else {
      return new EntityCollectionIdentityOperator<>();
    }
  }
}
