package org.liara.api.collection.configuration;

import org.liara.api.collection.filtering.EntityCollectionFilter;
import org.liara.api.collection.sorting.Sorts;
import org.liara.api.request.parser.APIRequestEntityCollectionFilterParser;
import org.liara.api.request.parser.APIRequestParser;

public final class EmptyCollectionRequestConfiguration<Entity> implements CollectionRequestConfiguration<Entity>
{
  @Override
  public APIRequestParser<EntityCollectionFilter<Entity>> createFilterParser () {
    return new APIRequestEntityCollectionFilterParser<>();
  }

  @Override
  public APIRequestParser<Sorts> createSortsParser () {
    return null;
  }
}
