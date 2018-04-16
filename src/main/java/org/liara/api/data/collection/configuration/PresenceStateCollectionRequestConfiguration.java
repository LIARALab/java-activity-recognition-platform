/*******************************************************************************
 * Copyright (C) 2018 C�dric DEMONGIVERT <cedric.demongivert@gmail.com>
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

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.state.PresenceState;
import org.liara.api.request.parser.filtering.APIRequestCompoundEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParser;
import org.liara.api.request.parser.filtering.APIRequestEntityFilterParserFactory;
import org.liara.api.request.parser.grouping.APIRequestGroupingProcessor;
import org.liara.api.request.parser.grouping.APIRequestGroupingProcessorFactory;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessor;
import org.liara.api.request.parser.ordering.APIRequestOrderingProcessorFactory;
import org.liara.api.request.validator.APIRequestFilterValidatorFactory;
import org.liara.api.request.validator.APIRequestValidator;

public final class PresenceStateCollectionRequestConfiguration implements CollectionRequestConfiguration<PresenceState>
{
  @Override
  public APIRequestEntityFilterParser<PresenceState> createFilterParser () {
    return new APIRequestCompoundEntityFilterParser<>(Arrays.asList(
      APIRequestEntityFilterParserFactory.integer("identifier", (root) -> root.get("_identifier")),
      APIRequestEntityFilterParserFactory.datetime("creationDate", (root) -> root.get("_creationDate")),
      APIRequestEntityFilterParserFactory.datetime("updateDate", (root) -> root.get("_updateDate")),
      APIRequestEntityFilterParserFactory.datetime("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestEntityFilterParserFactory.datetime("start", (root) -> root.get("_start")),
      APIRequestEntityFilterParserFactory.datetime("end", (root) -> root.get("_end")),
      APIRequestEntityFilterParserFactory.datetime("emittionDate", (root) -> root.get("_emittionDate")),
      APIRequestEntityFilterParserFactory.duration("duration", (root) -> root.get("_milliseconds")),
      APIRequestEntityFilterParserFactory.datetimeInRange("date", (root) -> root.get("_start"), (root) -> root.get("_end")),
      APIRequestEntityFilterParserFactory.joinCollection("node", "_node", NodeCollection.class),
      APIRequestEntityFilterParserFactory.joinCollection("sensor", "_sensor", SensorCollection.class)
    ));
  }

  @Override
  public List<APIRequestValidator> createFilterValidators () {
    return Arrays.asList(
      APIRequestFilterValidatorFactory.integer("identifier"),
      APIRequestFilterValidatorFactory.datetime("creationDate"),
      APIRequestFilterValidatorFactory.datetime("updateDate"),
      APIRequestFilterValidatorFactory.datetime("deletionDate"),
      APIRequestFilterValidatorFactory.datetime("start"),
      APIRequestFilterValidatorFactory.datetime("end"),
      APIRequestFilterValidatorFactory.datetime("emittionDate"),
      APIRequestFilterValidatorFactory.duration("duration"),
      APIRequestFilterValidatorFactory.datetimeInRange("date"),
      APIRequestFilterValidatorFactory.joinCollection("node", NodeCollection.class),
      APIRequestFilterValidatorFactory.joinCollection("sensor", SensorCollection.class)
    );
  }

  @Override
  public List<APIRequestOrderingProcessor<PresenceState>> createOrderingProcessors () {
    return Arrays.asList(
      APIRequestOrderingProcessorFactory.field("identifier", (root) -> root.get("_identifier")),
      APIRequestOrderingProcessorFactory.field("creationDate", (root) -> root.get("_creationDate")),
      APIRequestOrderingProcessorFactory.field("updateDate", (root) -> root.get("_updateDate")),
      APIRequestOrderingProcessorFactory.field("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestOrderingProcessorFactory.field("start", (root) -> root.get("_start")),
      APIRequestOrderingProcessorFactory.field("end", (root) -> root.get("_end")),
      APIRequestOrderingProcessorFactory.field("emittionDate", (root) -> root.get("_emittionDate")),
      APIRequestOrderingProcessorFactory.field("duration", (root) -> root.get("_milliseconds")),
      APIRequestOrderingProcessorFactory.field("date", (root) -> root.get("_start")),
      APIRequestOrderingProcessorFactory.joinCollection("node", "_node", NodeCollection.class),
      APIRequestOrderingProcessorFactory.joinCollection("sensor", "_sensor", SensorCollection.class)
    );
  }

  @Override
  public List<APIRequestGroupingProcessor<PresenceState>> createGroupingProcessors () {
    return Arrays.asList(
      APIRequestGroupingProcessorFactory.expression("identifier", (root) -> root.get("_identifier")),
      APIRequestGroupingProcessorFactory.expression("creationDate", (root) -> root.get("_creationDate")),
      APIRequestGroupingProcessorFactory.expression("updateDate", (root) -> root.get("_updateDate")),
      APIRequestGroupingProcessorFactory.expression("deletionDate", (root) -> root.get("_deletionDate")),
      APIRequestGroupingProcessorFactory.expression("start", (root) -> root.get("_start")),
      APIRequestGroupingProcessorFactory.expression("end", (root) -> root.get("_end")),
      APIRequestGroupingProcessorFactory.expression("emittionDate", (root) -> root.get("_emittionDate")),
      APIRequestGroupingProcessorFactory.expression("duration", (root) -> root.get("_milliseconds")),
      APIRequestGroupingProcessorFactory.expression("date", (root) -> root.get("_start")),
      APIRequestGroupingProcessorFactory.joinCollection("node", "_node", NodeCollection.class),
      APIRequestGroupingProcessorFactory.joinCollection("sensor", "_sensor", SensorCollection.class)
    );
  }
}