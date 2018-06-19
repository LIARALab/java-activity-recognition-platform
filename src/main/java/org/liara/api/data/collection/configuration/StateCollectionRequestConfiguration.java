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
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.data.collection.SensorCollection;
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

public final class StateCollectionRequestConfiguration implements CollectionRequestConfiguration<State>
{
  @NonNull
  private final SimpleEntityFieldSelector<State, Join<State, Sensor>> _sensorJoin = root -> root.join("_sensor");
  
  @NonNull
  private final ApplicationEntityCollectionRequestConfiguration _applicationEntityConfiguration = new ApplicationEntityCollectionRequestConfiguration();
  
  @Override
  public APIRequestEntityCollectionOperatorParser<State> createFilterParser () {
    return new APIRequestEntityCollectionConjunctionOperatorParser<>(
      Arrays.asList(
        APIRequestEntityFilterParserFactory.integer(
          "identifier", (root) -> root.get("_identifier")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "creationDate", (root) -> root.get("_creationDate")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "deletionDate", (root) -> root.get("_deletionDate")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "updateDate", (root) -> root.get("_updateDate")
        ),
        APIRequestEntityFilterParserFactory.datetime(
          "emittionDate", (root) -> root.get("_emittionDate")
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
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("date"),
      APIRequestFilterValidatorFactory.datetime("emittionDate"),
      APIRequestFilterValidatorFactory.includeCollection("sensor", SensorCollection.class)
    );
  }

  @Override
  public List<APIRequestOrderingProcessor<State>> createOrderingProcessors () {
    return Arrays.asList(
      APIRequestOrderingProcessorFactory.field(
        "identifier", (root) -> root.get("_identifier")
      ),
      APIRequestOrderingProcessorFactory.field(
        "creationDate", (root) -> root.get("_creationDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "deletionDate", (root) -> root.get("_deletionDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "updateDate", (root) -> root.get("_updateDate")
      ),
      APIRequestOrderingProcessorFactory.field(
        "emittionDate", (root) -> root.get("_emittionDate")
      ),
      APIRequestOrderingProcessorFactory.joinCollection(
        "sensor", _sensorJoin, SensorCollection.class
      )   
    );
  }
  
  @Override
  public List<APIRequestGroupingProcessor<State>> createGroupingProcessors () {
    return Arrays.asList(
      APIRequestGroupingProcessorFactory.expression("identifier", (root) -> root.get("_identifier")),
      APIRequestGroupingProcessorFactory.expression("creationDate", (root) -> root.get("_creationDate")),
      APIRequestGroupingProcessorFactory.expression("updateDate", (root) -> root.get("_updateDate")),
      APIRequestGroupingProcessorFactory.expression("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestGroupingProcessorFactory.expression("emittionDate", (root) -> root.get("_emittionDate")),
      APIRequestGroupingProcessorFactory.joinCollection("sensor", _sensorJoin, SensorCollection.class)
    );
  }
}
