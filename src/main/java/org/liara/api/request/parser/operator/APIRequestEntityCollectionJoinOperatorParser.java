package org.liara.api.request.parser.operator;

import javax.persistence.criteria.Join;

import org.liara.api.collection.operator.EntityCollectionJoinOperator;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestEntityCollectionJoinOperatorParser<Entity, Joined> 
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final EntityFieldSelector<Entity, Join<Entity, Joined>> _join;
  
  @NonNull
  private final APIRequestEntityCollectionOperatorParser<Joined> _joinParser;

  public APIRequestEntityCollectionJoinOperatorParser (
    @NonNull final String field,
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join,
    @NonNull final APIRequestEntityCollectionOperatorParser<Joined> joinParser 
  ) {
    _field = field;
    _join = join;
    _joinParser = joinParser;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_field);
    
    if (subRequest.getParameterCount() > 0) {
      return new EntityCollectionJoinOperator<>(
          _join,
          _joinParser.parse(subRequest)
      );
    } else {
      return new EntityCollectionIdentityOperator<>();
    }
  }
}
