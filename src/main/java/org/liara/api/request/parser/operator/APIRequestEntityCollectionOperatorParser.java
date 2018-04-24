package org.liara.api.request.parser.operator;

import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.parser.APIRequestParser;

public interface APIRequestEntityCollectionOperatorParser<Entity>
       extends APIRequestParser<EntityCollectionOperator<Entity>>
{ }
