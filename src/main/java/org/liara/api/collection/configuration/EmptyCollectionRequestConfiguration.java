package org.liara.api.collection.configuration;

import java.util.Collections;
import java.util.List;

import org.liara.api.request.parser.filtering.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParser;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessor;

public final class EmptyCollectionRequestConfiguration<Entity> implements CollectionRequestConfiguration<Entity>
{
  @Override
  public APIRequestEntityFilterParser<Entity> createFilterParser () {
    return new APIRequestCompoundEntityFilterParser<>();
  }

  @Override
  public List<APIRequestOrderingProcessor<Entity>> createOrderingProcessors () {
    return Collections.emptyList();
  }
}
