package org.liara.api.request.parser.operator;

import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestEntityCollectionIfPresentOperatorParser<Entity>
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final APIRequestEntityCollectionOperatorParser<Entity> _parser;
  
  public APIRequestEntityCollectionIfPresentOperatorParser (
    @NonNull final String parameter,
    @NonNull final APIRequestEntityCollectionOperatorParser<Entity> parser
  ) {
    _parameter = parameter;
    _parser = parser;
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    if (request.contains(_parameter)) {
      return _parser.parse(request);
    }
    
    return new EntityCollectionIdentityOperator<>();
  }

}
