package org.liara.api.collection.configuration;

import java.util.Collections;
import java.util.List;

import org.liara.api.request.parser.operator.APIRequestEntityCollectionOperatorParser;
import org.liara.api.request.parser.operator.ordering.APIRequestOrderingProcessor;

public final class EmptyCollectionRequestConfiguration<Entity> implements CollectionRequestConfiguration<Entity>
{
  @Override
  public APIRequestEntityCollectionOperatorParser<Entity> createFilterParser () {
    return null;
  }

  @Override
  public List<APIRequestOrderingProcessor<Entity>> createOrderingProcessors () {
    return Collections.emptyList();
  }

  /*
  @Override
  public List<APIRequestGroupingProcessor<Entity>> createGroupingProcessors () {
    return Collections.emptyList();
  }
  */
}
