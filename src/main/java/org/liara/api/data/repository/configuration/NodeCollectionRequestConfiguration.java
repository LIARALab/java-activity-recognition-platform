package org.liara.api.data.repository.configuration;

import java.util.Arrays;
import java.util.Collection;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.filtering.EntityCollectionFilter;
import org.liara.api.collection.sorting.Sorts;
import org.liara.api.data.entity.Node;
import org.liara.api.request.parser.APIRequestEntityCollectionFilterParser;
import org.liara.api.request.parser.APIRequestEntityFieldFilterParserFactory;
import org.liara.api.request.parser.APIRequestParser;
import org.liara.api.request.validator.APIRequestFilterValidatorFactory;
import org.liara.api.request.validator.APIRequestValidator;

public class NodeCollectionRequestConfiguration implements CollectionRequestConfiguration<Node>
{
  @Override
  public APIRequestParser<EntityCollectionFilter<Node>> createFilterParser () {
    return new APIRequestEntityCollectionFilterParser<>(Arrays.asList(
      APIRequestEntityFieldFilterParserFactory.integer("identifier", (root) -> root.get("_identifier")),
      APIRequestEntityFieldFilterParserFactory.datetime("creationDate", (root) -> root.get("_creationDate")),
      APIRequestEntityFieldFilterParserFactory.datetime("updateDate", (root) -> root.get("_updateDate")),
      APIRequestEntityFieldFilterParserFactory.datetime("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestEntityFieldFilterParserFactory.integer("start", (root) -> root.get("_start")),
      APIRequestEntityFieldFilterParserFactory.integer("end", (root) -> root.get("_end")),
      APIRequestEntityFieldFilterParserFactory.text("name", (root) -> root.get("_name"))
    ));
  }

  @Override
  public APIRequestParser<Sorts> createSortsParser () {
    return null;
  }

  @Override
  public Collection<APIRequestValidator> createFilterValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.integer("start"),
      APIRequestFilterValidatorFactory.integer("end"),
      APIRequestFilterValidatorFactory.text("name")
    );
  }
}
