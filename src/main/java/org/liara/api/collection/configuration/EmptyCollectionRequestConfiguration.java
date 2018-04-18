package org.liara.api.collection.configuration;

import java.util.Collections;
import java.util.List;

import org.liara.api.request.parser.filtering.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityCollectionFilteringOperatorParser;
import org.liara.api.request.parser.grouping.APIRequestGroupingProcessor;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessor;

public final class EmptyCollectionRequestConfiguration<Entity> implements CollectionRequestConfiguration<Entity>
{
  @Override
  public APIRequestEntityCollectionFilteringOperatorParser<Entity> createFilterParser () {
    return new APIRequestCompoundEntityFilterParser<>();
  }

  @Override
  public List<APIRequestOrderingProcessor<Entity>> createOrderingProcessors () {
    return Collections.emptyList();
  }

  @Override
  public List<APIRequestGroupingProcessor<Entity>> createGroupingProcessors () {
    return Collections.emptyList();
  }
}
