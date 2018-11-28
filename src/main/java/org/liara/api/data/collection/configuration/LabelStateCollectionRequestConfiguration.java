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

import org.liara.api.collection.Operators;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.LabelState;
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

import javax.persistence.criteria.Join;
import java.util.Arrays;
import java.util.List;

public final class LabelStateCollectionRequestConfiguration
  implements CollectionRequestConfiguration<LabelState>
{
  @NonNull
  private final SimpleEntityFieldSelector<LabelState, Join<LabelState, Node>> _nodeJoin = root -> root.join("_node");
  
  @NonNull
  private final SimpleEntityFieldSelector<LabelState, Join<LabelState, Sensor>> _sensorJoin = root -> root.join(
    "_sensor");
  
  @Override
  public APIRequestEntityCollectionOperatorParser<LabelState> createFilterParser () {
    return new APIRequestEntityCollectionConjunctionOperatorParser<>(
      Arrays.asList(
        APIRequestEntityFilterParserFactory.integer(
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
        APIRequestEntityFilterParserFactory.datetime(
          "start", (root) -> root.get("_start")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "end", (root) -> root.get("_start")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "emittionDate", (root) -> root.get("_emittionDate")
        ),
        APIRequestEntityFilterParserFactory.duration("duration", LabelState.DURATION_SELECTOR
        ),
        APIRequestEntityFilterParserFactory.text(
          "tag", root -> root.get("_tag")
        ),
        APIRequestEntityFilterParserFactory.callback(
          "duration", request -> Operators.notNull("_end")
        ),
        APIRequestEntityFilterParserFactory.datetimeInRange(
          "date", 
          (root) -> root.get("_start"), 
          (root) -> root.get("_end")
        ),
        APIRequestEntityFilterParserFactory.joinCollection(
          "node", _nodeJoin, NodeCollection.class
        ),
        APIRequestEntityFilterParserFactory.joinCollection(
          "sensor", _sensorJoin, SensorCollection.class
        )
     )
    );
  }

  @Override
  public List<APIRequestValidator> createFilteringValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.datetime("start"),
      APIRequestFilterValidatorFactory.datetime("end"),
      APIRequestFilterValidatorFactory.datetime("emittionDate"),
      APIRequestFilterValidatorFactory.duration("duration"),
      APIRequestFilterValidatorFactory.text("tag"),
      APIRequestFilterValidatorFactory.datetimeInRange("date"),
      APIRequestFilterValidatorFactory.includeCollection("node", NodeCollection.class),
      APIRequestFilterValidatorFactory.includeCollection("sensor", SensorCollection.class)
    );
  }

  @Override
  public List<APIRequestOrderingProcessor<LabelState>> createOrderingProcessors () {
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
        "start", (root) -> root.get("_start")
      ),
      APIRequestOrderingProcessorFactory.field(
        "end", (root) -> root.get("_end")
      ),
      APIRequestOrderingProcessorFactory.field(
        "emittionDate", (root) -> root.get("_emittionDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "emittionDate", (root) -> root.get("_tag")
      ),
      APIRequestOrderingProcessorFactory.field(
        "duration",
        (query, queried) -> LabelState.DURATION_SELECTOR.select(query, queried)
      ),
      APIRequestOrderingProcessorFactory.field(
        "date", (root) -> root.get("_start")
      ),
      APIRequestOrderingProcessorFactory.joinCollection(
        "node", _nodeJoin, NodeCollection.class
      ),
      APIRequestOrderingProcessorFactory.joinCollection(
        "sensor", _sensorJoin, SensorCollection.class
      )
    );
  }

  @Override
  public List<APIRequestGroupingProcessor<LabelState>> createGroupingProcessors () {
    return Arrays.asList(
      APIRequestGroupingProcessorFactory.expression("identifier", (root) -> root.get("_identifier")),
      APIRequestGroupingProcessorFactory.expression("creationDate", (root) -> root.get("_creationDate")),
      APIRequestGroupingProcessorFactory.expression("updateDate", (root) -> root.get("_updateDate")),
      APIRequestGroupingProcessorFactory.expression("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestGroupingProcessorFactory.expression("start", (root) -> root.get("_start")),
      APIRequestGroupingProcessorFactory.expression("end", (root) -> root.get("_end")),
      APIRequestGroupingProcessorFactory.expression("emittionDate", (root) -> root.get("_emittionDate")),
      APIRequestGroupingProcessorFactory.expression("tag", (root) -> root.get("_tag")),
      APIRequestGroupingProcessorFactory.expression("emittionDate:date",
        (query, root) -> query.getManager().getCriteriaBuilder().function("DATE_FORMAT",
          String.class,
          root.get("_emittionDate"),
          query.getManager().getCriteriaBuilder().literal("%Y-%m-%d")
        )
      ),
      APIRequestGroupingProcessorFactory.expression(
        "duration",
        (query, queried) -> LabelState.DURATION_SELECTOR.select(query, queried)
      ),
      APIRequestGroupingProcessorFactory.expression("nights",
        (query, queried) -> LabelState.NIGHTS_SELECTOR.select(query, queried)
      ),
      APIRequestGroupingProcessorFactory.joinCollection("node", _nodeJoin, NodeCollection.class),
      APIRequestGroupingProcessorFactory.joinCollection("sensor", _sensorJoin, SensorCollection.class)
    );
  }
}
