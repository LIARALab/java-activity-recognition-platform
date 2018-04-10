package org.liara.api.data.repository.configuration;

import java.util.Arrays;
import java.util.Collection;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.sorting.Sorts;
import org.liara.api.data.entity.Node;
import org.liara.api.request.parser.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.APIRequestEntityFilterParser;
import org.liara.api.request.parser.APIRequestEntityFilterParserFactory;
import org.liara.api.request.parser.APIRequestParser;
import org.liara.api.request.validator.APIRequestFilterValidatorFactory;
import org.liara.api.request.validator.APIRequestValidator;

public class NodeCollectionRequestConfiguration implements CollectionRequestConfiguration<Node>
{
  @Override
  public APIRequestEntityFilterParser<Node> createFilterParser () {
    return new APIRequestCompoundEntityFilterParser<>(Arrays.asList(
      APIRequestEntityFilterParserFactory.integer("identifier", (root) -> root.get("_identifier")),
      APIRequestEntityFilterParserFactory.datetime("creationDate", (root) -> root.get("_creationDate")),
      APIRequestEntityFilterParserFactory.datetime("updateDate", (root) -> root.get("_updateDate")),
      APIRequestEntityFilterParserFactory.datetime("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestEntityFilterParserFactory.integer("start", (root) -> root.get("_start")),
      APIRequestEntityFilterParserFactory.integer("end", (root) -> root.get("_end")),
      APIRequestEntityFilterParserFactory.text("name", (root) -> root.get("_name"))
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
