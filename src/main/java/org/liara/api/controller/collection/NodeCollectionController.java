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
package org.liara.api.controller.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.controller.model.BaseNodeModelController;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.schema.NodeSchema;
import org.liara.api.event.NodeEvent;
import org.liara.rest.error.InvalidModelException;
import org.liara.rest.metamodel.collection.PostableCollection;
import org.liara.rest.metamodel.collection.RootRestCollection;
import org.liara.rest.metamodel.model.RestModel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;

/**
 * A controller for all API endpoints that read, write or patch information about nodes.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
@RootRestCollection("nodes")
public final class NodeCollectionController
  extends BaseNodeCollectionController
  implements PostableCollection<Node>
{
  @NonNull
  private final WritableControllerConfiguration _configuration;

  @Autowired
  public NodeCollectionController (
    @NonNull final WritableControllerConfiguration configuration
  )
  {
    super(configuration);
    _configuration = configuration;
  }

  @Override
  public @NonNull Long post (
    @NonNull @Valid final JsonNode json
  )
  throws JsonProcessingException, InvalidModelException
  {
    @NonNull final NodeSchema schema = _configuration.getObjectMapper().treeToValue(json, NodeSchema.class);

    _configuration.assertIsValid(schema);
    _configuration.getApplicationEventPublisher().publishEvent(new NodeEvent.Create(this, schema));

    return schema.getIdentifier();
  }

  @Override
  public @NonNull RestModel<Node> getModelController () {
    return new BaseNodeModelController(_configuration);
  }

  /*
  @ChildCollection(
    name = "sensors",
    type = "sensors"
  )
  public @NonNull Operator getSensors (@NonNull final Long identifier) {
    return Filter.expression(":this.nodeIdentifier = :identifier")
                 .setParameter("identifier", identifier);
  }
*/

  /*
  @GetMapping("/nodes/{identifier}/sensors")
  public ResponseEntity<List<Sensor>> getSensors (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) {
    final Node node = _collection.findByIdentifierOrFail(identifier);

    return indexCollection(_sensors.in(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/sensors/count")
  @ParametersFromConfiguration(
    value = SensorCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countSensors (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_sensors.in(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/sensors/{sensorIdentifier}")
  public Sensor getSensor (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long sensorIdentifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);

    return _sensors.in(node).findByIdentifierOrFail(sensorIdentifier);
  }

  @GetMapping("/nodes/{identifier}/deepSensors")
  @ParametersFromConfiguration(
    value = SensorCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Sensor>> getDeepSensors (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);

    return indexCollection(_sensors.deepIn(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/deepSensors/count")
  @ParametersFromConfiguration(
    value = SensorCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countDeepSensors (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_sensors.deepIn(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/deepSensors/{sensorIdentifier}")
  public Sensor getDeepSensor (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long sensorIdentifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);

    return _sensors.deepIn(node).findByIdentifierOrFail(sensorIdentifier);
  }

  @GetMapping("/nodes/{identifier}/children")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Node>> getDirectChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return indexCollection(_collection.directChildrenOf(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/children/count")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countDirectChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_collection.directChildrenOf(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/children/{childrenIdentifer}")
  public Node getDirectChild (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long childrenIdentifer
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);
    return _collection.directChildrenOf(node).findByIdentifierOrFail(childrenIdentifer);
  }
  
  @GetMapping("/nodes/{identifier}/allChildren")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Node>> getDeepChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return indexCollection(_collection.deepChildrenOf(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/allChildren/count")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countDeepChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_collection.deepChildrenOf(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/allChildren/{childrenIdentifer}")
  public Node getDeepChild (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long childrenIdentifer
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);
    return _collection.deepChildrenOf(node).findByIdentifierOrFail(childrenIdentifer);
  }
  */
}
