package org.liara.api.collection.configuration;

import org.liara.api.collection.sorting.Sorts;
import org.liara.api.request.parser.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.APIRequestEntityFilterParser;
import org.liara.api.request.parser.APIRequestParser;

public final class EmptyCollectionRequestConfiguration<Entity> implements CollectionRequestConfiguration<Entity>
{
  @Override
  public APIRequestEntityFilterParser<Entity> createFilterParser () {
    return new APIRequestCompoundEntityFilterParser<>();
  }

  @Override
  public APIRequestParser<Sorts> createSortsParser () {
    return null;
  }
}
