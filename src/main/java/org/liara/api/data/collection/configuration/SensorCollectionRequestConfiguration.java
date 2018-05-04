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
import java.util.List;

import javax.persistence.criteria.Join;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.relation.JoinRelation;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
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

public final class SensorCollectionRequestConfiguration implements CollectionRequestConfiguration<Sensor>
{
  @NonNull
  private final SimpleEntityFieldSelector<Sensor, Join<Sensor, Node>> _nodeJoin = root -> root.join("_node");
  
  @Override
  public APIRequestEntityCollectionOperatorParser<Sensor> createFilterParser () {
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
        APIRequestEntityFilterParserFactory.text(
          "name", (root) -> root.get("_name")
        ),
        APIRequestEntityFilterParserFactory.booleanValue(
          "virtual", (root) -> root.get("_virtual")
        ),
        APIRequestEntityFilterParserFactory.text("type", (root) -> root.get("_type")),
        APIRequestEntityFilterParserFactory.text("valueType", (root) -> root.get("_valueType")),
        APIRequestEntityFilterParserFactory.text("valueLabel", (root) -> root.get("_valueLabel")),
        APIRequestEntityFilterParserFactory.text("valueUnit", (root) -> root.get("_valueUnit")),
        APIRequestEntityFilterParserFactory.text("ipv4Address", (root) -> root.get("_ipv4Address")),
        APIRequestEntityFilterParserFactory.existsCollection(
          "states", 
          State.class,
          new JoinRelation<Sensor, State>(root -> root.join("_states")), 
          StateCollection.class
        ),
        APIRequestEntityFilterParserFactory.joinCollection(
          "node",
          _nodeJoin, 
          NodeCollection.class
        )
      )
    );
  }

  @Override
  public List<APIRequestValidator> createFilteringValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.text("name"),
      APIRequestFilterValidatorFactory.booleanValue("virtual"),
      APIRequestFilterValidatorFactory.includeCollection("states", StateCollection.class),
      APIRequestFilterValidatorFactory.includeCollection("node", NodeCollection.class) 
    );
  }

  @Override
  public List<APIRequestOrderingProcessor<Sensor>> createOrderingProcessors () {
    return Arrays.asList(
      APIRequestOrderingProcessorFactory.field(
        "identifier", (root) -> root.get("_identifier")
      ),
      APIRequestOrderingProcessorFactory.field(
        "creationDate", (root) -> root.get("_creationDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "updateDate", (root) -> root.get("_updateDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "deletionDate", (root) -> root.get("_deletionDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "name", (root) -> root.get("_name")
      ),
      APIRequestOrderingProcessorFactory.field(
        "type", (root) -> root.get("_type")
      ),
      APIRequestOrderingProcessorFactory.field(
        "valueType", (root) -> root.get("_valueType")
      ),
      APIRequestOrderingProcessorFactory.field(
        "valueLabel", (root) -> root.get("_valueLabel")
      ),
      APIRequestOrderingProcessorFactory.field(
        "valueUnit", (root) -> root.get("_valueUnit")
      ),
      APIRequestOrderingProcessorFactory.field(
        "ipv4Address", (root) -> root.get("_ipv4Address")
      ),
      APIRequestOrderingProcessorFactory.field(
        "virtual", (root) -> root.get("_virtual")
      ),
      APIRequestOrderingProcessorFactory.joinCollection(
        "node", _nodeJoin, NodeCollection.class
      )
    );
  }
  
  @Override
  public List<APIRequestGroupingProcessor<Sensor>> createGroupingProcessors () {
    return Arrays.asList(
      APIRequestGroupingProcessorFactory.expression("identifier", (root) -> root.get("_identifier")),
      APIRequestGroupingProcessorFactory.expression("creationDate", (root) -> root.get("_creationDate")),
      APIRequestGroupingProcessorFactory.expression("updateDate", (root) -> root.get("_updateDate")),
      APIRequestGroupingProcessorFactory.expression("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestGroupingProcessorFactory.expression("name", (root) -> root.get("_name")),
      APIRequestGroupingProcessorFactory.expression("type", (root) -> root.get("_type")),
      APIRequestGroupingProcessorFactory.expression("valueType", (root) -> root.get("_valueType")),
      APIRequestGroupingProcessorFactory.expression("valueLabel", (root) -> root.get("_valueLabel")),
      APIRequestGroupingProcessorFactory.expression("valueUnit", (root) -> root.get("_valueUnit")),
      APIRequestGroupingProcessorFactory.expression("ipv4Address", (root) -> root.get("_ipv4Address")),
      APIRequestGroupingProcessorFactory.expression("virtual", (root) -> root.get("_virtual")),
      APIRequestGroupingProcessorFactory.joinCollection(
        "node", _nodeJoin, NodeCollection.class
      )
    );
  }
}
