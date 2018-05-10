package org.liara.api.request.parser.operator;

import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface APIRequestEntityCollectionCallbackOperatorParser<Entity> 
       extends APIRequestEntityCollectionOperatorParser<Entity>
{
  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request);
}
