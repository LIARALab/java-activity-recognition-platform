/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.data.collection.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubquery;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.request.parser.operator.APIRequestEntityCollectionConjunctionOperatorParser;
import org.liara.api.request.parser.operator.APIRequestEntityCollectionOperatorParser;
import org.liara.api.request.parser.operator.APIRequestEntityFilterParserFactory;
import org.liara.api.request.parser.operator.ordering.APIRequestOrderingProcessor;
import org.liara.api.request.parser.operator.ordering.APIRequestOrderingProcessorFactory;
import org.liara.api.request.parser.transformation.grouping.APIRequestGroupingProcessor;
import org.liara.api.request.parser.transformation.grouping.APIRequestGroupingProcessorFactory;
import org.liara.api.request.validator.APIRequestFilterValidatorFactory;
import org.liara.api.request.validator.APIRequestValidator;
import org.springframework.lang.NonNull;

public class NodeCollectionRequestConfiguration implements CollectionRequestConfiguration<Node>
{
  private void nodeParentsRelation (
    @NonNull final EntityCollectionQuery<Node, ?> parent,
    @NonNull final EntityCollectionSubquery<Node, Node> children
  ) {
    final Path<Node> related = children.correlate(parent.getEntity());
    final CriteriaBuilder builder = children.getManager().getCriteriaBuilder();
    
    children.andWhere(builder.and(
      builder.lessThan(children.getEntity().get("_setStart"), related.get("_setStart")),
      builder.greaterThan(children.getEntity().get("_setEnd"), related.get("_setEnd"))
    ));
  }
  
  private void nodeChildrenRelation (
    @NonNull final EntityCollectionQuery<Node, ?> parent,
    @NonNull final EntityCollectionSubquery<Node, Node> children
  ) {
    final Path<Node> related = children.correlate(parent.getEntity());
    final CriteriaBuilder builder = children.getManager().getCriteriaBuilder();
    
    children.where(builder.and(
      builder.greaterThan(children.getEntity().get("_setStart"), related.get("_setStart")),
      builder.lessThan(children.getEntity().get("_setEnd"), related.get("_setEnd"))
    ));
  }
  
  @Override
  public APIRequestEntityCollectionOperatorParser<Node> createFilterParser () {
    return new APIRequestEntityCollectionConjunctionOperatorParser<>(
      Arrays.asList(
        APIRequestEntityFilterParserFactory.integerValue(
          "identifier", (root) -> root.get("_identifier")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "creationDate", (root) -> root.get("_creationDate")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "updateDate", (root) -> root.get("_updateDate")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "deletionDate", (root) -> root.get("_deletionDate")
        ),
        APIRequestEntityFilterParserFactory.integerValue(
          "setStart", (root) -> root.get("_setStart")
        ),
        APIRequestEntityFilterParserFactory.integerValue(
          "setEnd", (root) -> root.get("_setEnd")
        ),
        APIRequestEntityFilterParserFactory.integerValue(
          "depth", (root) -> root.get("_depth")
        ),
        APIRequestEntityFilterParserFactory.text(
          "name", (root) -> root.get("_name")
        ),
        APIRequestEntityFilterParserFactory.text(
          "type", (root) -> root.get("_type")
        ),
        APIRequestEntityFilterParserFactory.existsCollection(
          "parents", Node.class, this::nodeParentsRelation, NodeCollection.class
        ),
        APIRequestEntityFilterParserFactory.existsCollection(
          "children", Node.class, this::nodeChildrenRelation, NodeCollection.class
        )
      )
    );
  }

  @Override
  public Collection<APIRequestValidator> createFilteringValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.integer("setStart"),
      APIRequestFilterValidatorFactory.integer("setEnd"),
      APIRequestFilterValidatorFactory.integer("depth"),
      APIRequestFilterValidatorFactory.text("name"),
      APIRequestFilterValidatorFactory.text("type"),
      APIRequestFilterValidatorFactory.includeCollection("parents", NodeCollection.class)
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
      APIRequestOrderingProcessorFactory.field("name", (root) -> root.get("_name")),
      APIRequestOrderingProcessorFactory.field("type", (root) -> root.get("_type"))
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
      APIRequestGroupingProcessorFactory.expression("name", (root) -> root.get("_name")),
      APIRequestGroupingProcessorFactory.expression("type", (root) -> root.get("_type"))
    );
  }
}
