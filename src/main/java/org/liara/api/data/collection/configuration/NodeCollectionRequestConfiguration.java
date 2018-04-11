package org.liara.api.data.collection.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.ordering.ComposedOrdering;
import org.liara.api.data.entity.Node;
import org.liara.api.request.parser.APIRequestParser;
import org.liara.api.request.parser.filtering.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParserFactory;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessor;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessorFactory;
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

  @Override
  public List<APIRequestOrderingProcessor<Node>> createOrderingProcessors () {
    return Arrays.asList(
      APIRequestOrderingProcessorFactory.field("identifier", (root) -> root.get("_identifier")),
      APIRequestOrderingProcessorFactory.field("creationDate", (root) -> root.get("_creationDate")),
      APIRequestOrderingProcessorFactory.field("updateDate", (root) -> root.get("_updateDate")),
      APIRequestOrderingProcessorFactory.field("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestOrderingProcessorFactory.field("start", (root) -> root.get("_start")),
      APIRequestOrderingProcessorFactory.field("end", (root) -> root.get("_end")),
      APIRequestOrderingProcessorFactory.field("name", (root) -> root.get("_name"))
    );
  }
}
