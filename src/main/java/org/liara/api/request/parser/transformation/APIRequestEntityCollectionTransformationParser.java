package org.liara.api.request.parser.transformation;

import org.liara.api.collection.transformation.EntityCollectionTransformation;
import org.liara.api.request.parser.APIRequestParser;

public interface APIRequestEntityCollectionTransformationParser<Input, Output> 
       extends   APIRequestParser<EntityCollectionTransformation<Input, Output>>
{ }
