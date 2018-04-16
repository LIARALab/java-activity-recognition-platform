package org.liara.api.data.collection.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.ordering.ComposedOrdering;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubQuery;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.request.parser.APIRequestParser;
import org.liara.api.request.parser.filtering.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParserFactory;
import org.liara.api.request.parser.grouping.APIRequestGroupingProcessor;
import org.liara.api.request.parser.grouping.APIRequestGroupingProcessorFactory;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessor;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessorFactory;
import org.liara.api.request.validator.APIRequestFilterValidatorFactory;
import org.liara.api.request.validator.APIRequestValidator;
import org.springframework.lang.NonNull;

public class NodeCollectionRequestConfiguration implements CollectionRequestConfiguration<Node>
{
  private void applyNodeParentsRelation (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Node, ?> master,
    @NonNull final EntityCollectionSubQuery<Node, ?, Node, ?> slave
  ) {
    final Path<Node> related = master.correlateEntity(slave);
    
    slave.where(builder.and(
      builder.lessThan(slave.getEntity().get("_setStart"), related.get("_setStart")),
      builder.greaterThan(slave.getEntity().get("_setEnd"), related.get("_setEnd"))
    ));
  }
  
  private void applyNodeChildrenRelation (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Node, ?> master,
    @NonNull final EntityCollectionSubQuery<Node, ?, Node, ?> slave
  ) {
    final Path<Node> related = master.correlateEntity(slave);
    
    slave.where(builder.and(
      builder.greaterThan(slave.getEntity().get("_setStart"), related.get("_setStart")),
      builder.lessThan(slave.getEntity().get("_setEnd"), related.get("_setEnd"))
    ));
  }
  
  @Override
  public APIRequestEntityFilterParser<Node> createFilterParser () {
    return new APIRequestCompoundEntityFilterParser<>(Arrays.asList(
      APIRequestEntityFilterParserFactory.integer("identifier", (root) -> root.get("_identifier")),
      APIRequestEntityFilterParserFactory.datetime("creationDate", (root) -> root.get("_creationDate")),
      APIRequestEntityFilterParserFactory.datetime("updateDate", (root) -> root.get("_updateDate")),
      APIRequestEntityFilterParserFactory.datetime("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestEntityFilterParserFactory.integer("setStart", (root) -> root.get("_setStart")),
      APIRequestEntityFilterParserFactory.integer("setEnd", (root) -> root.get("_setEnd")),
      APIRequestEntityFilterParserFactory.integer("depth", (root) -> root.get("_depth")),
      APIRequestEntityFilterParserFactory.text("name", (root) -> root.get("_name")),
      APIRequestEntityFilterParserFactory.customHavingCollection(
        "parents", Node.class, this::applyNodeParentsRelation, NodeCollection.class
      ),
      APIRequestEntityFilterParserFactory.customHavingCollection(
        "children", Node.class, this::applyNodeChildrenRelation, NodeCollection.class
      )
    ));
  }

  @Override
  public Collection<APIRequestValidator> createFilterValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.integer("setStart"),
      APIRequestFilterValidatorFactory.integer("setEnd"),
      APIRequestFilterValidatorFactory.integer("depth"),
      APIRequestFilterValidatorFactory.text("name"),
      APIRequestFilterValidatorFactory.customHavingCollection("parents", NodeCollection.class)
    );
  }

  @Override
  public List<APIRequestOrderingProcessor<Node>> createOrderingProcessors () {
    return Arrays.asList(
      APIRequestOrderingProcessorFactory.field("identifier", (root) -> root.get("_identifier")),
      APIRequestOrderingProcessorFactory.field("creationDate", (root) -> root.get("_creationDate")),
      APIRequestOrderingProcessorFactory.field("updateDate", (root) -> root.get("_updateDate")),
      APIRequestOrderingProcessorFactory.field("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestOrderingProcessorFactory.field("setStart", (root) -> root.get("_setStart")),
      APIRequestOrderingProcessorFactory.field("setEnd", (root) -> root.get("_setEnd")),
      APIRequestOrderingProcessorFactory.field("depth", (root) -> root.get("_depth")),
      APIRequestOrderingProcessorFactory.field("name", (root) -> root.get("_name"))
    );
  }
  
  @Override
  public List<APIRequestGroupingProcessor<Node>> createGroupingProcessors () {
    return Arrays.asList(
      APIRequestGroupingProcessorFactory.expression("identifier", (root) -> root.get("_identifier")),
      APIRequestGroupingProcessorFactory.expression("creationDate", (root) -> root.get("_creationDate")),
      APIRequestGroupingProcessorFactory.expression("updateDate", (root) -> root.get("_updateDate")),
      APIRequestGroupingProcessorFactory.expression("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestGroupingProcessorFactory.expression("setStart", (root) -> root.get("_setStart")),
      APIRequestGroupingProcessorFactory.expression("setEnd", (root) -> root.get("_setEnd")),
      APIRequestGroupingProcessorFactory.expression("depth", (root) -> root.get("_depth")),
      APIRequestGroupingProcessorFactory.expression("name", (root) -> root.get("_name"))
    );
  }
}
