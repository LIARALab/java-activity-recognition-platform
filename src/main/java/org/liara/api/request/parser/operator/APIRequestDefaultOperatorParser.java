package org.liara.api.request.parser.operator;

import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.parser.APIRequestDefaultValueParser;
import org.liara.api.request.parser.APIRequestParser;
import org.springframework.lang.NonNull;

public class      APIRequestDefaultOperatorParser<Entity>
       extends    APIRequestDefaultValueParser<EntityCollectionOperator<Entity>>
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  public APIRequestDefaultOperatorParser(
    @NonNull final String parameter,
    @NonNull final EntityCollectionOperator<Entity> defaultValue,
    @NonNull final APIRequestParser<EntityCollectionOperator<Entity>> wrapped
  ) { super(parameter, defaultValue, wrapped); } 
}
